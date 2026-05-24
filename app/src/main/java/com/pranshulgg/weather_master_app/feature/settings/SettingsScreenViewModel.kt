package com.pranshulgg.weather_master_app.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.core.model.weather.DistanceUnit
import com.pranshulgg.weather_master_app.core.model.weather.PrecipitationUnit
import com.pranshulgg.weather_master_app.core.model.weather.PressureUnit
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit
import com.pranshulgg.weather_master_app.data.repository.WeatherUnitsRepository
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

    fun updateTemperatureUnit(tempUnit: TemperatureUnit) {
        viewModelScope.launch {
            weatherUnitsRepo.updateTemperatureUnit(tempUnit)
        }
    }

    fun updatePressureUnit(pressureUnit: PressureUnit) {
        viewModelScope.launch {
            weatherUnitsRepo.updatePressureUnit(pressureUnit)
        }
    }

    fun updateWindSpeedUnit(windSpeedUnit: WindSpeedUnit) {
        viewModelScope.launch {
            weatherUnitsRepo.updateWindSpeedUnit(windSpeedUnit)
        }
    }


    fun updateDistanceUnit(distanceUnit: DistanceUnit) {
        viewModelScope.launch {
            weatherUnitsRepo.updateDistanceUnit(distanceUnit)
        }

    }

    fun updatePrecipitationUnit(precipitationUnit: PrecipitationUnit) {
        viewModelScope.launch {
            weatherUnitsRepo.updatePrecipitationUnit(precipitationUnit)
        }
    }


}