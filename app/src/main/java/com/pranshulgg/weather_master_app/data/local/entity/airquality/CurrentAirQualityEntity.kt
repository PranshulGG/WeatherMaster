package com.pranshulgg.weather_master_app.data.local.entity.airquality

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.pranshulgg.weather_master_app.data.local.entity.location.WeatherLocationEntity


@Entity(
    tableName = "air_quality_current", foreignKeys = [
        ForeignKey(
            entity = WeatherLocationEntity::class,
            parentColumns = ["id"],
            childColumns = ["locationId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [Index("locationId")],
    primaryKeys = ["locationId"]
)
data class CurrentAirQualityEntity(

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

