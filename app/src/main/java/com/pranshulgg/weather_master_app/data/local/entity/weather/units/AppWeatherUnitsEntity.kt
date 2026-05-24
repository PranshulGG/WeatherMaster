package com.pranshulgg.weather_master_app.data.local.entity.weather.units

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pranshulgg.weather_master_app.core.model.weather.DistanceUnit
import com.pranshulgg.weather_master_app.core.model.weather.PrecipitationUnit
import com.pranshulgg.weather_master_app.core.model.weather.PressureUnit
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit

@Entity("weather_units")
data class AppWeatherUnitsEntity(
    @PrimaryKey val id: Int = 1,
    val tempUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val windUnit: WindSpeedUnit = WindSpeedUnit.KPH,
    val distanceUnit: DistanceUnit = DistanceUnit.KM,
    val pressureUnit: PressureUnit = PressureUnit.HPA,
    val precipitationUnit: PrecipitationUnit = PrecipitationUnit.MM,
)