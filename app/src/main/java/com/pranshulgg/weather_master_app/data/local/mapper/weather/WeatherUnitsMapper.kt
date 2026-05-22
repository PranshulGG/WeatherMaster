package com.pranshulgg.weather_master_app.data.local.mapper.weather

import com.pranshulgg.weather_master_app.data.local.entity.weather.units.AppWeatherUnitsEntity
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits

fun AppWeatherUnitsEntity.toDomain(): WeatherUnits =
    WeatherUnits(
        tempUnit = tempUnit,
        windUnit = windUnit,
        distanceUnit = distanceUnit,
        pressureUnit = pressureUnit,
        precipitationUnit = precipitationUnit
    )


fun WeatherUnits.toEntity(): AppWeatherUnitsEntity =
    AppWeatherUnitsEntity(
        tempUnit = tempUnit,
        windUnit = windUnit,
        distanceUnit = distanceUnit,
        pressureUnit = pressureUnit,
        precipitationUnit = precipitationUnit
    )