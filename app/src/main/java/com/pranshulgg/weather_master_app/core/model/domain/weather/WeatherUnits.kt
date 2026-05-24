package com.pranshulgg.weather_master_app.core.model.domain.weather

import com.pranshulgg.weather_master_app.core.model.weather.DistanceUnit
import com.pranshulgg.weather_master_app.core.model.weather.PrecipitationUnit
import com.pranshulgg.weather_master_app.core.model.weather.PressureUnit
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit

data class WeatherUnits(
    val tempUnit: TemperatureUnit,
    val windUnit: WindSpeedUnit,
    val distanceUnit: DistanceUnit,
    val pressureUnit: PressureUnit,
    val precipitationUnit: PrecipitationUnit,
) {
    companion object {
        fun getDefault() = WeatherUnits(
            tempUnit = TemperatureUnit.CELSIUS,
            windUnit = WindSpeedUnit.KPH,
            distanceUnit = DistanceUnit.KM,
            pressureUnit = PressureUnit.HPA,
            precipitationUnit = PrecipitationUnit.MM
        )
    }
}