package com.pranshulgg.weather_master_app.feature.editlocation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.sources.AirQualitySource
import com.pranshulgg.weather_master_app.core.model.sources.AlertSource
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EditLocationViewModel @Inject constructor(
    private val locationsRepo: LocationsRepository,
) : ViewModel() {

    private var _uiState = mutableStateOf(EditLocationScreenUiState())
    val uiState: State<EditLocationScreenUiState> = _uiState

    fun getLocationForId(id: String) {
        viewModelScope.launch {
            val location = locationsRepo.getLocationForId(id)
            _uiState.value = _uiState.value.copy(location = location)
        }
    }


    fun saveLocationName(name: String?, id: String) {
        viewModelScope.launch {
            locationsRepo.updateLocationCustomName(id, name)
        }
    }

    fun showWeatherSourcesForLocationSheet() {
        _uiState.value = _uiState.value.copy(isWeatherSourcesForLocationSheetOpen = true)
    }

    fun hideWeatherSourcesForLocationSheet() {
        _uiState.value = _uiState.value.copy(isWeatherSourcesForLocationSheetOpen = false)
    }

    fun updateSelectedWeatherSource(source: WeatherSource) {
        _uiState.value = _uiState.value.copy(selectedWeatherSource = source)
    }

    fun updateSelectedAlertSource(source: AlertSource) {
        _uiState.value = _uiState.value.copy(selectedAlertSource = source)
    }

    fun updateSelectedAirQualitySource(source: AirQualitySource) {
        _uiState.value = _uiState.value.copy(selectedAirQualitySource = source)
    }

    fun showAlertSourcesSheet() {
        _uiState.value = _uiState.value.copy(isAlertSourcesSheetOpen = true)
    }

    fun hideAlertSourcesSheet() {
        _uiState.value = _uiState.value.copy(isAlertSourcesSheetOpen = false)
    }

    fun showAirQualitySourcesSheet() {
        _uiState.value = _uiState.value.copy(isAirQualitySourcesSheetOpen = true)
    }

    fun hideAirQualitySourcesSheet() {
        _uiState.value = _uiState.value.copy(isAirQualitySourcesSheetOpen = false)
    }

    fun showEditLocationNameSheet() {
        _uiState.value = _uiState.value.copy(isEditLocationNameSheetOpen = true)
    }

    fun hideEditLocationNameSheet() {
        _uiState.value = _uiState.value.copy(isEditLocationNameSheetOpen = false)
    }
}