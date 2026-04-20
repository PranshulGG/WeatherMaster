package com.pranshulgg.weathermaster.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weathermaster.core.model.DistanceUnits
import com.pranshulgg.weathermaster.core.model.PrecipitationUnits
import com.pranshulgg.weathermaster.core.model.PressureUnits
import com.pranshulgg.weathermaster.core.model.TemperatureUnits
import com.pranshulgg.weathermaster.core.model.WindSpeedUnits
import com.pranshulgg.weathermaster.data.repository.AppWeatherUnitsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val weatherUnitsRepo: AppWeatherUnitsRepository
) : ViewModel() {


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