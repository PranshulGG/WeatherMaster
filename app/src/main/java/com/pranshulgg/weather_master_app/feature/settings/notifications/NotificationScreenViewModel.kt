package com.pranshulgg.weather_master_app.feature.settings.notifications

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherUnitsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import kotlinx.coroutines.launch


@HiltViewModel
class NotificationScreenViewModel @Inject constructor(
    private val locationsRepo: LocationsRepository,
    private val weatherUnitsRepository: WeatherUnitsRepository
) : ViewModel() {
    private var _uiState = mutableStateOf(NotificationsScreenUiState())
    val uiState: State<NotificationsScreenUiState> = _uiState

    fun getWeather(locationId: String?) {

        if (locationId == null) return

        viewModelScope.launch {
            val data = locationsRepo.getWeatherForLocation(locationId)
            _uiState.value = _uiState.value.copy(weather = data)
        }
    }

    fun getUnitsOnce() {
        viewModelScope.launch {
            val units = weatherUnitsRepository.getUnitsOnce()
            _uiState.value = _uiState.value.copy(units = units ?: WeatherUnits.getDefault())
        }
    }

    fun getDefaultLocation() {
        viewModelScope.launch {
            val location = locationsRepo.getLocationsOnce().find { it.isDefault }
            _uiState.value = _uiState.value.copy(defaultLocation = location)
        }
    }
}