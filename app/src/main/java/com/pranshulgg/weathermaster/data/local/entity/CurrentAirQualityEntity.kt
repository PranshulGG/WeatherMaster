package com.pranshulgg.weathermaster.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "air_quality_current")
data class CurrentAirQualityEntity(

    @PrimaryKey
    val locationId: String,

    val usAqi: Int?,
    val pm10: Double?,
    val pm25: Double?,
    val carbonMonoxide: Double?,
    val nitrogenDioxide: Double?,
    val sulphurDioxide: Double?,
    val ozone: Double?,
    val lastUpdatedInMilli: Long
)

