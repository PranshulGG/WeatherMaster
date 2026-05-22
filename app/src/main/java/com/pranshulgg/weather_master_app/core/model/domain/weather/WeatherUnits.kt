package com.pranshulgg.weather_master_app.core.model.domain.weather

import com.pranshulgg.weather_master_app.core.model.weather.DistanceUnits
import com.pranshulgg.weather_master_app.core.model.weather.PrecipitationUnits
import com.pranshulgg.weather_master_app.core.model.weather.PressureUnits
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnits
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnits

data class WeatherUnits(
    val tempUnit: TemperatureUnits,
    val windUnit: WindSpeedUnits,
    val distanceUnit: DistanceUnits,
    val pressureUnit: PressureUnits,
    val precipitationUnit: PrecipitationUnits,
) {
    companion object {
        fun getDefault() = WeatherUnits(
            tempUnit = TemperatureUnits.CELSIUS,
            windUnit = WindSpeedUnits.KPH,
            distanceUnit = DistanceUnits.KM,
            pressureUnit = PressureUnits.HPA,
            precipitationUnit = PrecipitationUnits.MM
        )
    }
}