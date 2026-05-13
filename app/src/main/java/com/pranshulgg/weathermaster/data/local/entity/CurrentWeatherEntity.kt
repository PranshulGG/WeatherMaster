package com.pranshulgg.weathermaster.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.pranshulgg.weathermaster.core.model.weather.WeatherConditions

@Entity(
    tableName = "weather_current",
    foreignKeys = [
        ForeignKey(
            entity = WeatherLocationEntity::class,
            parentColumns = ["id"],
            childColumns = ["locationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("locationId")],
    primaryKeys = ["locationId"]
)
data class CurrentWeatherEntity(
    val locationId: String,

    val temperature: Double,
    val humidity: Double,
    val windSpeed: Double,
    val windDirection: Int?,
    val pressureMsl: Double,
    val visibility: Int?,
    val cloudCover: Double?,
    val uvIndex: Double,
    val weatherCondition: WeatherConditions,
    val feelsLike: Double?,
    val time: Long,
    val dewPoint: Double?,

    val utcOffsetSeconds: Long,

    val lastUpdatedSecs: Long
)