package com.pranshulgg.weather_master_app.feature.locations

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.core.managers.LocationManager
import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toMessageRes
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weather_master_app.data.repository.WeatherContextRepository
import com.pranshulgg.weather_master_app.data.store.LocationStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationsScreenViewModel @Inject constructor(
    private val weatherContextRepository: WeatherContextRepository,
    private val locationManager: LocationManager,
    locationStore: LocationStore
) : ViewModel() {

    val location = locationStore.data
    val weatherForTotalLocations = weatherContextRepository.getWeatherForTotalLocations().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = emptyList()
    )

    val alertsForTotalLocations = weatherContextRepository.getAlertsForTotalLocations().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = emptyList()
    )

    fun updateDefaultLocation(id: String) {
        viewModelScope.launch {
            weatherContextRepository.updateDefaultLocation(id)
        }
    }

    private val _uiState = mutableStateOf(LocationsScreenUiState())
    val uiState: State<LocationsScreenUiState> = _uiState


    fun showConfirmationDialog() {
        _uiState.value = _uiState.value.copy(isConfirmationDialogOpen = true)
    }

    fun hideConfirmationDialog() {
        _uiState.value = _uiState.value.copy(isConfirmationDialogOpen = false)
    }

    fun setLongClickedLocation(location: Location) {
        _uiState.value = _uiState.value.copy(longClickedLocation = location)
    }

    fun showBottomSheet(location: Location) {
        setLongClickedLocation(location)
        _uiState.value = _uiState.value.copy(isBottomSheetOpen = true)
    }

    fun hideBottomSheet() {
        _uiState.value = _uiState.value.copy(isBottomSheetOpen = false)
    }

    fun deleteLocation(id: String) {
        viewModelScope.launch {
            locationManager.deleteLocation(id)
        }
    }

    fun saveDeviceLocation() {
        _uiState.value = _uiState.value.copy(isDeviceLocationLoading = true)
        viewModelScope.launch {
            try {
                weatherContextRepository.saveDeviceLocation()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                SnackbarManager.show(AppException.CurrentLocationUnavailable().toMessageRes())
            } finally {
                _uiState.value = _uiState.value.copy(isDeviceLocationLoading = false)
            }
        }
    }

}