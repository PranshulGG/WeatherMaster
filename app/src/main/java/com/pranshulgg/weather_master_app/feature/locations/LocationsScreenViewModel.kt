package com.pranshulgg.weather_master_app.feature.locations

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.util.Logger
import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toMessageRes
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationsScreenViewModel @Inject constructor(
    private val locationsRepo: LocationsRepository
) : ViewModel() {

    val allLocationsWeather = locationsRepo.getWeatherForAllLocations().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun updateDefaultLocation(id: String) {
        viewModelScope.launch {
            locationsRepo.updateDefaultLocation(id)
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


    fun saveDeviceLocation() {
        _uiState.value = _uiState.value.copy(isDeviceLocationLoading = true)
        viewModelScope.launch {
            try {
                Log.d("COOL HERE", "CALLED HERE HERE")
                locationsRepo.saveDeviceLocation()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                SnackbarManager.show(AppException.CurrentLocationUnavailable().toMessageRes())
            } finally {
                _uiState.value = _uiState.value.copy(isDeviceLocationLoading = false)
            }
        }
    }

}