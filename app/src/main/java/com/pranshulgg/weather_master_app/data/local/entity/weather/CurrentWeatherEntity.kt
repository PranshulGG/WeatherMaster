package com.pranshulgg.weather_master_app.data.local.entity.weather

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.data.local.entity.location.WeatherLocationEntity

@Entity(
    tableName = "weather_current",
    foreignKeys = [
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
data class CurrentWeatherEntity(
    val locationId: String,

    val temperature: Double?,
    val humidity: Double,
    val windSpeed: Double?,
    val windDirection: WindDirection?,
    val pressureMsl: Double?,
    val visibility: Int?,
    val cloudCover: Double?,
    val uvIndex: Double?,
    val weatherCondition: WeatherCondition,
    val feelsLike: Double?,
    val time: Long,
    val dewPoint: Double?,

    val utcOffsetSeconds: Long?,


    val lastUpdatedInMilli: Long
)