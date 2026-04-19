package com.pranshulgg.weathermaster.feature.shared

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weathermaster.core.model.Location
import com.pranshulgg.weathermaster.core.model.Weather
import com.pranshulgg.weathermaster.core.model.WeatherProviders
import com.pranshulgg.weathermaster.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weathermaster.core.ui.state.ActiveLocationStore
import com.pranshulgg.weathermaster.data.provider.WeatherRepositoryProvider
import com.pranshulgg.weathermaster.data.repository.LocationsRepository
import com.pranshulgg.weathermaster.feature.main.MainScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import javax.inject.Inject
import androidx.compose.runtime.State
import kotlinx.coroutines.launch

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repo: WeatherRepositoryProvider,
    private val locationsRepo: LocationsRepository,
    private val activeLocationStore: ActiveLocationStore
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
    }


    fun getWeather(location: Location, provider: WeatherProviders) {
        val currentRepo = repo.getRepository(provider)
        _uiState.value = _uiState.value.copy(isError = false, isLoading = true)

        viewModelScope.launch {
            val data = try {
                currentRepo.getWeather(location)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                SnackbarManager.show("ERROR- $e")
                _uiState.value = _uiState.value.copy(isError = true, isLoading = false)
                return@launch
            }
            _uiState.value = _uiState.value.copy(weather = data, isLoading = false)
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

}
