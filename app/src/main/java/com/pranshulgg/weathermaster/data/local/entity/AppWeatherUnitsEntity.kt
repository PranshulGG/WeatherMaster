package com.pranshulgg.weathermaster.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pranshulgg.weathermaster.core.model.DistanceUnits
import com.pranshulgg.weathermaster.core.model.PrecipitationUnits
import com.pranshulgg.weathermaster.core.model.PressureUnits
import com.pranshulgg.weathermaster.core.model.TemperatureUnits
import com.pranshulgg.weathermaster.core.model.WindSpeedUnits

@Entity("weather_units")
data class AppWeatherUnitsEntity(
    @PrimaryKey val id: Int = 1,
    val tempUnit: TemperatureUnits = TemperatureUnits.CELSIUS,
    val windUnit: WindSpeedUnits = WindSpeedUnits.KPH,
    val distanceUnit: DistanceUnits = DistanceUnits.KM,
    val pressureUnit: PressureUnits = PressureUnits.HPA,
    val precipitationUnit: PrecipitationUnits = PrecipitationUnits.MM,
)