package com.pranshulgg.weather_master_app.data.local.entity.weather

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.data.local.entity.location.WeatherLocationEntity

@Entity(
    tableName = "weather_hourly",
    foreignKeys = [
        ForeignKey(
            entity = WeatherLocationEntity::class,
            parentColumns = ["id"],
            childColumns = ["locationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("locationId")],
    primaryKeys = ["locationId", "time"]
)
data class HourlyWeatherEntity(
    val locationId: String,

    val temperature: Double,
    val windSpeed: Double?,
    val windDirection: WindDirection?,
    val rain: Double,
    val snowfall: Double?,
    val uvIndex: Double?,
    val weatherCondition: WeatherCondition,
    val time: Long,
    val precipitationProbability: Int?
)