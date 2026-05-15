package com.pranshulgg.weathermaster.feature.daily

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.pranshulgg.weathermaster.data.repository.LocationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weathermaster.core.model.domain.AppWeatherUnits
import com.pranshulgg.weathermaster.core.model.domain.WeatherBlock
import com.pranshulgg.weathermaster.data.repository.AppWeatherUnitsRepository
import com.pranshulgg.weathermaster.data.repository.WeatherDataRepository
import com.pranshulgg.weathermaster.feature.main.MainScreenUiState
import kotlinx.coroutines.launch

@HiltViewModel
class DailyScreenViewModel @Inject constructor(
    private val locationsRepo: LocationsRepository,
    private val weatherUnitsRepository: AppWeatherUnitsRepository,
    private val weatherDataRepository: WeatherDataRepository
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
            _uiState.value = _uiState.value.copy(units = units ?: AppWeatherUnits.getDefault())
        }
    }


    // TODO: Duplicate from `WeatherViewModel`
    fun loadBlocks() {
        viewModelScope.launch {
            val loadedBlocks = weatherDataRepository.loadBlocks()
            _uiState.value = _uiState.value.copy(blocks = loadedBlocks)
        }
    }

    fun updateBlocksOrder(blocks: List<WeatherBlock>) {
        _uiState.value = _uiState.value.copy(blocks = blocks)
    }

}