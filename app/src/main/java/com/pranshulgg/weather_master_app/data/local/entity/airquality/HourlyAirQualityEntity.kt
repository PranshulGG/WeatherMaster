package com.pranshulgg.weather_master_app.data.local.entity.airquality

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.pranshulgg.weather_master_app.data.local.entity.location.WeatherLocationEntity


@Entity(
    tableName = "air_quality_hourly", foreignKeys = [
        ForeignKey(
            entity = WeatherLocationEntity::class,
            parentColumns = ["id"],
            childColumns = ["locationId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [Index("locationId")]
)
data class HourlyAirQualityEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val locationId: String,

    val pm10: Double?,
    val pm25: Double?,
    val carbonMonoxide: Double?,
    val nitrogenDioxide: Double?,
    val sulphurDioxide: Double?,
    val ozone: Double?,
    val time: Long
)

