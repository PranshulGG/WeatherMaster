package com.pranshulgg.weathermaster.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class WeatherWithRelations(
    @Embedded val location: WeatherLocationEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "locationId"
    )
    val current: CurrentWeatherEntity?,

    @Relation(
        parentColumn = "id",
        entityColumn = "locationId"
    )
    val hourly: List<HourlyWeatherEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "locationId"
    )
    val daily: List<DailyWeatherEntity>
)