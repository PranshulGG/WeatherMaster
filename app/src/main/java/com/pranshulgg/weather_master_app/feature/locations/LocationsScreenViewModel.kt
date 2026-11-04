package com.pranshulgg.weather_master_app.feature.locations

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.core.managers.LocationManager
import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.domain.toMessageRes
import com.pranshulgg.weather_master_app.core.model.sources.Capability
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.sources.getSourcesForCountry
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weather_master_app.data.repository.ApiKeysRepository
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
    private val apiKeysRepo: ApiKeysRepository,
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
                val resolved = weatherContextRepository.resolveDeviceLocation()
                val apiKeys = apiKeysRepo.getAllApiKeys()
                _uiState.value = _uiState.value.copy(
                    pendingDeviceLocation = resolved,
                    apiKeys = apiKeys,
                    isWeatherSourcesForLocationSheetOpen = true
                )
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                SnackbarManager.show(AppException.CurrentLocationUnavailable().toMessageRes())
            } finally {
                _uiState.value = _uiState.value.copy(isDeviceLocationLoading = false)
            }
        }
    }

    fun hideWeatherSourcesForLocationSheet() {
        _uiState.value = _uiState.value.copy(isWeatherSourcesForLocationSheetOpen = false)
    }

    fun confirmDeviceLocationSource(weatherSource: Source) {
        val resolved = _uiState.value.pendingDeviceLocation ?: return

        viewModelScope.launch {
            try {
                val alertSource = if (Capability.ALERTS in weatherSource.capabilities) {
                    weatherSource
                } else {
                    getSourcesForCountry(resolved.countryCode?.uppercase())
                        .firstOrNull { Capability.ALERTS in it.capabilities }
                        ?: resolved.alertSource
                }

                weatherContextRepository.saveLocation(
                    resolved.copy(
                        source = weatherSource,
                        alertSource = alertSource
                    )
                )
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                SnackbarManager.show(e.toAppException().toMessageRes())
            } finally {
                _uiState.value = _uiState.value.copy(
                    isWeatherSourcesForLocationSheetOpen = false,
                    pendingDeviceLocation = null
                )
            }
        }
    }

}