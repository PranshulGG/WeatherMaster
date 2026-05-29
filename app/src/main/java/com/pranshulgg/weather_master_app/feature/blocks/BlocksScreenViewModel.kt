package com.pranshulgg.weather_master_app.feature.blocks

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherUnitsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlocksScreenViewModel @Inject constructor(
    private val locationsRepo: LocationsRepository,
    private val weatherUnitsRepository: WeatherUnitsRepository
) : ViewModel() {

    private var _uiState = mutableStateOf(BlockScreenUiState())
    val uiState: State<BlockScreenUiState> = _uiState


    fun getWeather(locationId: String) {
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
}
