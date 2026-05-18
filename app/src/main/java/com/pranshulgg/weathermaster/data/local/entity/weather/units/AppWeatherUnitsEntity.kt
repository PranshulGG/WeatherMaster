package com.pranshulgg.weathermaster.data.local.entity.weather.units

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pranshulgg.weathermaster.core.model.weather.DistanceUnits
import com.pranshulgg.weathermaster.core.model.weather.PrecipitationUnits
import com.pranshulgg.weathermaster.core.model.weather.PressureUnits
import com.pranshulgg.weathermaster.core.model.weather.TemperatureUnits
import com.pranshulgg.weathermaster.core.model.weather.WindSpeedUnits

@Entity("weather_units")
data class AppWeatherUnitsEntity(
    @PrimaryKey val id: Int = 1,
    val tempUnit: TemperatureUnits = TemperatureUnits.CELSIUS,
    val windUnit: WindSpeedUnits = WindSpeedUnits.KPH,
    val distanceUnit: DistanceUnits = DistanceUnits.KM,
    val pressureUnit: PressureUnits = PressureUnits.HPA,
    val precipitationUnit: PrecipitationUnits = PrecipitationUnits.MM,
)