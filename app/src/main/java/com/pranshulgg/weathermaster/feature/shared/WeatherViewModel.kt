package com.pranshulgg.weathermaster.feature.shared

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weathermaster.core.model.domain.Location
import com.pranshulgg.weathermaster.core.model.WeatherProviders
import com.pranshulgg.weathermaster.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weathermaster.core.ui.state.ActiveLocationStore
import com.pranshulgg.weathermaster.data.provider.WeatherRepositoryProvider
import com.pranshulgg.weathermaster.data.repository.LocationsRepository
import com.pranshulgg.weathermaster.feature.main.MainScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import javax.inject.Inject
import androidx.compose.runtime.State
import com.pranshulgg.weathermaster.core.model.DistanceUnits
import com.pranshulgg.weathermaster.core.model.PrecipitationUnits
import com.pranshulgg.weathermaster.core.model.PressureUnits
import com.pranshulgg.weathermaster.core.model.TemperatureUnits
import com.pranshulgg.weathermaster.core.model.WeatherResult
import com.pranshulgg.weathermaster.core.model.WindSpeedUnits
import com.pranshulgg.weathermaster.core.model.domain.AppWeatherUnits
import com.pranshulgg.weathermaster.data.repository.AppWeatherUnitsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repo: WeatherRepositoryProvider,
    private val locationsRepo: LocationsRepository,
    private val activeLocationStore: ActiveLocationStore,
    private val appWeatherUnitsRepo: AppWeatherUnitsRepository
) : ViewModel() {

    private var _uiState = mutableStateOf(MainScreenUiState())
    val uiState: State<MainScreenUiState> = _uiState


    init {
        // LOAD DEFAULT ON START
        locationsRepo.getDefaultLocation()
            .filterNotNull()
            .take(1)
            .onEach { location ->
                if (activeLocationStore.active.value?.id != location.id) {
                    activeLocationStore.set(location)
                }
            }
            .launchIn(viewModelScope)

        // KEEP TRACK OF ACTIVE LOCATION
        activeLocationStore.active.filterNotNull().distinctUntilChanged().onEach {
            _uiState.value = _uiState.value.copy(activeLocation = it)
            getWeather(it, it.provider)
        }.launchIn(viewModelScope)


        locationsRepo.getLocations()
            .onEach { locations ->
                _uiState.value = _uiState.value.copy(locations = locations)
            }
            .launchIn(viewModelScope)


        appWeatherUnitsRepo.getUnits().distinctUntilChanged().onEach {
            _uiState.value = _uiState.value.copy(weatherUnits = it ?: AppWeatherUnits.getDefault())
        }.launchIn(viewModelScope)
    }


    fun getWeather(location: Location, provider: WeatherProviders, isRefresh: Boolean = false) {
        _uiState.value = _uiState.value.copy(isError = false, isLoading = true)
        val currentRepo = repo.getRepository(provider)

        viewModelScope.launch {
            val result = currentRepo.getWeather(location, isRefresh)
            when (result) {
                is WeatherResult.Success -> {
                    _uiState.value =
                        _uiState.value.copy(weather = result.weather, isLoading = false)
                }

                is WeatherResult.Error -> {
                    SnackbarManager.show("ERROR- ${result.message}")
                    _uiState.value = _uiState.value.copy(isError = true, isLoading = false)
                }

                is WeatherResult.RefreshNotAvailable -> {
                    SnackbarManager.show("Please wait 15mins before refreshing")
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }

        }

    }


    fun deleteLocation(id: String) {
        viewModelScope.launch {
            locationsRepo.deleteLocation(id)
        }
    }

    fun setActiveLocation(location: Location) {
        activeLocationStore.set(location)
    }

    val appWeatherUnits = appWeatherUnitsRepo.getUnits().distinctUntilChanged().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = (AppWeatherUnits(
            TemperatureUnits.CELSIUS, WindSpeedUnits.KPH, DistanceUnits.KM,
            PressureUnits.HPA, PrecipitationUnits.MM
        ))
    )

}
