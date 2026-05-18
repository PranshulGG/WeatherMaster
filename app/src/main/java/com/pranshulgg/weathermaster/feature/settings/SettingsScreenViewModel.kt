package com.pranshulgg.weathermaster.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weathermaster.core.model.weather.DistanceUnits
import com.pranshulgg.weathermaster.core.model.weather.PrecipitationUnits
import com.pranshulgg.weathermaster.core.model.weather.PressureUnits
import com.pranshulgg.weathermaster.core.model.weather.TemperatureUnits
import com.pranshulgg.weathermaster.core.model.weather.WindSpeedUnits
import com.pranshulgg.weathermaster.data.repository.WeatherUnitsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val weatherUnitsRepo: WeatherUnitsRepository
) : ViewModel() {

    val weatherUnits = weatherUnitsRepo.getUnits().stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun updateTemperatureUnit(tempUnit: TemperatureUnits) {
        viewModelScope.launch {
            weatherUnitsRepo.updateTemperatureUnit(tempUnit)
        }
    }

    fun updatePressureUnit(pressureUnit: PressureUnits) {
        viewModelScope.launch {
            weatherUnitsRepo.updatePressureUnit(pressureUnit)
        }
    }

    fun updateWindSpeedUnit(windSpeedUnit: WindSpeedUnits) {
        viewModelScope.launch {
            weatherUnitsRepo.updateWindSpeedUnit(windSpeedUnit)
        }
    }


    fun updateDistanceUnit(distanceUnit: DistanceUnits) {
        viewModelScope.launch {
            weatherUnitsRepo.updateDistanceUnit(distanceUnit)
        }

    }

    fun updatePrecipitationUnit(precipitationUnit: PrecipitationUnits) {
        viewModelScope.launch {
            weatherUnitsRepo.updatePrecipitationUnit(precipitationUnit)
        }
    }


}