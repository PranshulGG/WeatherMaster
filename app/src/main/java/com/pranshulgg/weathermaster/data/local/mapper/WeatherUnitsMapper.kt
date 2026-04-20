package com.pranshulgg.weathermaster.data.local.mapper

import com.pranshulgg.weathermaster.data.local.entity.AppWeatherUnitsEntity
import com.pranshulgg.weathermaster.core.model.domain.AppWeatherUnits

fun AppWeatherUnitsEntity.toDomain(): AppWeatherUnits =
    AppWeatherUnits(
        tempUnit = tempUnit,
        windUnit = windUnit,
        distanceUnit = distanceUnit,
        pressureUnit = pressureUnit,
        precipitationUnit = precipitationUnit
    )


fun AppWeatherUnits.toEntity(): AppWeatherUnitsEntity =
    AppWeatherUnitsEntity(
        tempUnit = tempUnit,
        windUnit = windUnit,
        distanceUnit = distanceUnit,
        pressureUnit = pressureUnit,
        precipitationUnit = precipitationUnit
    )