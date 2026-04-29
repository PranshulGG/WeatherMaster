package com.pranshulgg.weathermaster.feature.shared

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weathermaster.core.model.domain.WeatherBlockType
import com.pranshulgg.weathermaster.core.model.WeatherProviders
import com.pranshulgg.weathermaster.core.model.WeatherResult
import com.pranshulgg.weathermaster.core.model.domain.Location
import com.pranshulgg.weathermaster.core.model.domain.WeatherBlock
import com.pranshulgg.weathermaster.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weathermaster.core.ui.state.ActiveLocationStore
import com.pranshulgg.weathermaster.data.provider.WeatherRepositoryProvider
import com.pranshulgg.weathermaster.data.repository.AppWeatherUnitsRepository
import com.pranshulgg.weathermaster.data.repository.LocationsRepository
import com.pranshulgg.weathermaster.data.repository.WeatherDataRepository
import com.pranshulgg.weathermaster.feature.main.MainScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repo: WeatherRepositoryProvider,
    private val locationsRepo: LocationsRepository,
    private val activeLocationStore: ActiveLocationStore,
    private val appWeatherUnitsRepo: AppWeatherUnitsRepository,
    private val weatherDataRepository: WeatherDataRepository
) : ViewModel() {

    private var _uiState = mutableStateOf(MainScreenUiState())
    val uiState: State<MainScreenUiState> = _uiState


    var blocks by mutableStateOf(emptyList<WeatherBlock>())
        private set


    init {
        // LOAD DEFAULT ON START
        if (activeLocationStore.active.value == null) {
            locationsRepo.getDefaultLocation()
                .filterNotNull()
                .take(1)
                .onEach { location ->
                    if (activeLocationStore.active.value?.id != location.id) {
                        activeLocationStore.set(location)
                    }
                }
                .launchIn(viewModelScope)
        }

        // KEEP TRACK OF ACTIVE LOCATION
        activeLocationStore.active.filterNotNull().distinctUntilChanged().onEach {
            _uiState.value =
                _uiState.value.copy(activeLocation = it)
            getWeather(it, it.provider)
        }.launchIn(viewModelScope)


        // KEEP TRACK OF ALL LOCATIONS
        locationsRepo.getLocations()
            .onEach { locations ->
                _uiState.value = _uiState.value.copy(locations = locations)
            }
            .launchIn(viewModelScope)


        // KEEP TRACK OF APP UNITS
        appWeatherUnitsRepo.getUnits().distinctUntilChanged().onEach {
            _uiState.value = _uiState.value.copy(weatherUnits = it)
        }.launchIn(viewModelScope)

        if (blocks.isEmpty()) {
            loadBlocksOrder()
        }
    }


    fun getWeather(location: Location, provider: WeatherProviders, isRefresh: Boolean = false) {
        setLoading(true)
        val startTime = System.currentTimeMillis()
        _uiState.value = _uiState.value.copy(isError = false)

        val currentRepo = repo.getRepository(provider)

        viewModelScope.launch {
            when (val result = currentRepo.getWeather(location, isRefresh)) {

                is WeatherResult.Success -> {
                    _uiState.value = _uiState.value.copy(weather = result.weather)
                }

                is WeatherResult.Error -> {

                    SnackbarManager.show("Error occurred. Please try again")

                    _uiState.value =
                        _uiState.value.copy(isError = true, weather = result.cacheWeather)
                }

                is WeatherResult.RefreshNotAvailable -> {
                    SnackbarManager.show("Please wait 15mins before refreshing")
                }
            }

            val elapsed = System.currentTimeMillis() - startTime
            val minLoadingTime = 1000L // 1s

            // Prevents loader flicker when responses return too quickly
            if (elapsed < minLoadingTime) {
                delay(minLoadingTime - elapsed)
            }

            setLoading(false)
        }

    }


    fun deleteLocation(id: String) {
        viewModelScope.launch {
            locationsRepo.deleteLocation(id)
        }
    }


    fun setLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }

    fun setActiveLocation(location: Location) {
        activeLocationStore.set(location)
    }


    fun saveBlocksOrder(
        items: List<WeatherBlock>
    ) {
        blocks = items

        viewModelScope.launch {
            weatherDataRepository.saveBlocks(items.map {
                WeatherBlock(
                    type = it.type,
                    isHidden = false,
                    position = it.position
                )
            })

        }

    }

    fun loadBlocksOrder() {
        viewModelScope.launch {
            val loadedBlocks = weatherDataRepository.loadBlocks()
            blocks = loadedBlocks
        }
    }

}
