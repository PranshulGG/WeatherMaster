package com.pranshulgg.weather_master_app.data.local.entity.airquality

import androidx.room.Embedded
import androidx.room.Relation

data class AirQualityWithRelations(
    @Embedded val current: CurrentAirQualityEntity?,

    @Relation(
        parentColumn = "locationId",
        entityColumn = "locationId"
    )
    val hourly: List<HourlyAirQualityEntity>
)