package com.pranshulgg.weathermaster.feature.daily

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weathermaster.core.model.domain.weather.WeatherBlock
import com.pranshulgg.weathermaster.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weathermaster.data.repository.LocationsRepository
import com.pranshulgg.weathermaster.data.repository.WeatherBlocksRepository
import com.pranshulgg.weathermaster.data.repository.WeatherUnitsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyScreenViewModel @Inject constructor(
    private val locationsRepo: LocationsRepository,
    private val weatherBlocksRepository: WeatherBlocksRepository,
    private val weatherUnitsRepository: WeatherUnitsRepository
) : ViewModel() {

    private var _uiState = mutableStateOf(DailyScreenUiState())
    val uiState: State<DailyScreenUiState> = _uiState


    fun getDailyWeather(locationId: String) {
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


    // TODO: Duplicate from `WeatherViewModel`
    fun loadBlocks() {
        viewModelScope.launch {
            val loadedBlocks = weatherBlocksRepository.loadBlocks(isDaily = true)
            _uiState.value = _uiState.value.copy(blocks = loadedBlocks)
        }
    }

    fun updateBlocksOrder(blocks: List<WeatherBlock>) {
        _uiState.value = _uiState.value.copy(blocks = blocks)
    }

}