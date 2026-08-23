package com.pranshulgg.weather_master_app.feature.editlocation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.openmeteo.OpenMeteoModel
import com.pranshulgg.weather_master_app.data.repository.ApiKeysRepository
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EditLocationViewModel @Inject constructor(
    private val locationsRepo: LocationsRepository,
    private val apiKeysRepo: ApiKeysRepository
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
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                apiKeys = apiKeysRepo.getAllApiKeys(),
                isWeatherSourcesForLocationSheetOpen = true
            )
        }
    }

    fun hideWeatherSourcesForLocationSheet() {
        _uiState.value = _uiState.value.copy(isWeatherSourcesForLocationSheetOpen = false)
    }

    fun updateSelectedWeatherSource(source: Source) {
        _uiState.value = _uiState.value.copy(selectedWeatherSource = source)
    }

    fun updateSelectedAlertSource(source: Source) {
        _uiState.value = _uiState.value.copy(selectedAlertSource = source)
    }

    fun updateSelectedAirQualitySource(source: Source) {
        _uiState.value = _uiState.value.copy(selectedAirQualitySource = source)
    }

    fun updateSelectedOpenMeteoModel(model: OpenMeteoModel) {
        _uiState.value = _uiState.value.copy(selectedOpenMeteoModel = model)
    }

    fun showAlertSourcesSheet() {
        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                apiKeys = apiKeysRepo.getAllApiKeys(),
                isAlertSourcesSheetOpen = true
            )
        }
    }

    fun hideAlertSourcesSheet() {
        _uiState.value = _uiState.value.copy(isAlertSourcesSheetOpen = false)
    }

    fun showAirQualitySourcesSheet() {
        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                apiKeys = apiKeysRepo.getAllApiKeys(),
                isAirQualitySourcesSheetOpen = true
            )
        }
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

    fun showConfirmationDialog() {
        _uiState.value = _uiState.value.copy(isConfirmationDialogOpen = true)
    }

    fun hideConfirmationDialog() {
        _uiState.value = _uiState.value.copy(isConfirmationDialogOpen = false)
    }

    fun showOpenMeteoModelsSheet() {
        _uiState.value = _uiState.value.copy(isOpenMeteoModelsSheetOpen = true)
    }

    fun hideOpenMeteoModelsSheet() {
        _uiState.value = _uiState.value.copy(isOpenMeteoModelsSheetOpen = false)
    }

    fun updateDefaultLocation(id: String) {
        viewModelScope.launch {
            locationsRepo.updateDefaultLocation(id)
        }
    }


}