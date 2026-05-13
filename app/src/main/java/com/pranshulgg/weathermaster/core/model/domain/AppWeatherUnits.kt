package com.pranshulgg.weathermaster.core.model.domain

import com.pranshulgg.weathermaster.core.model.weather.DistanceUnits
import com.pranshulgg.weathermaster.core.model.weather.PrecipitationUnits
import com.pranshulgg.weathermaster.core.model.weather.PressureUnits
import com.pranshulgg.weathermaster.core.model.weather.TemperatureUnits
import com.pranshulgg.weathermaster.core.model.weather.WindSpeedUnits

data class AppWeatherUnits(
    val tempUnit: TemperatureUnits,
    val windUnit: WindSpeedUnits,
    val distanceUnit: DistanceUnits,
    val pressureUnit: PressureUnits,
    val precipitationUnit: PrecipitationUnits,
) {
    companion object {
        fun getDefault() = AppWeatherUnits(
            tempUnit = TemperatureUnits.CELSIUS,
            windUnit = WindSpeedUnits.KPH,
            distanceUnit = DistanceUnits.KM,
            pressureUnit = PressureUnits.HPA,
            precipitationUnit = PrecipitationUnits.MM
        )
    }
}