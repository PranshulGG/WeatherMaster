package com.pranshulgg.weather_master_app.feature.alerts

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.data.repository.WeatherContextRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel

class AlertsScreenViewModel @Inject constructor(
    val weatherContextRepository: WeatherContextRepository,
    val locationRepository: WeatherContextRepository
) : ViewModel() {
    private var _uiState = mutableStateOf(AlertsScreenUiState())
    val uiState: State<AlertsScreenUiState> = _uiState

    fun getAlertsForLocation(locationId: String) {
        viewModelScope.launch {
            val alerts = weatherContextRepository.getAlertsForLocation(locationId)
            _uiState.value = _uiState.value.copy(alerts = alerts)
        }
    }

    fun getLocation(locationId: String) {
        viewModelScope.launch {
            val location = locationRepository.getLocationForId(locationId)
            _uiState.value = _uiState.value.copy(location = location)
        }
    }
}
