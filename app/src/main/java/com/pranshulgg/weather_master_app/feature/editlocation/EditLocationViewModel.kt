package com.pranshulgg.weather_master_app.feature.editlocation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.core.managers.LocationManager
import com.pranshulgg.weather_master_app.core.managers.SourceManager
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.openmeteo.OpenMeteoModel
import com.pranshulgg.weather_master_app.data.repository.ApiKeysRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherContextRepository
import com.pranshulgg.weather_master_app.data.store.LocationStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EditLocationViewModel @Inject constructor(
    private val weatherContextRepository: WeatherContextRepository,
    private val apiKeysRepo: ApiKeysRepository,
    private val locationManager: LocationManager,
    locationStore: LocationStore,
    private val sourceManager: SourceManager
) : ViewModel() {

    val locations = locationStore.data

    private var _uiState = mutableStateOf(EditLocationScreenUiState())
    val uiState: State<EditLocationScreenUiState> = _uiState

    fun saveLocationName(name: String?, id: String) {
        viewModelScope.launch {
            weatherContextRepository.updateLocationCustomName(id, name)
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
            weatherContextRepository.updateDefaultLocation(id)
        }
    }

    fun deleteLocation(id: String) {
        viewModelScope.launch {
            locationManager.deleteLocation(id)
        }
    }

    fun updateSources(
        location: Location,
        source: Source,
        airQualitySource: Source,
        alertSource: Source,
        openMeteoModel: OpenMeteoModel,
        onBack: () -> Unit
    ) {
        viewModelScope.launch {
            sourceManager.updateSources(
                location,
                source,
                airQualitySource,
                alertSource,
                openMeteoModel
            )

            onBack()
        }
    }
}