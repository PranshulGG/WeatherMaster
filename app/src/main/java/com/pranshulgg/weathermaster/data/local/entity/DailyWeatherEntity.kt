package com.pranshulgg.weathermaster.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.pranshulgg.weathermaster.core.model.astro.MoonPhase
import com.pranshulgg.weathermaster.core.model.weather.WeatherConditions

@Entity(
    tableName = "weather_daily",
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
data class DailyWeatherEntity(
    val locationId: String,

    val temperatureMin: Double,
    val temperatureMax: Double,
    val windSpeed: Double,
    val windDirection: Int?,
    val rainSum: Double,
    val snowfallSum: Double?,
    val uvIndexMax: Double,
    val weatherCondition: WeatherConditions,
    val time: Long,
    val precipitationProbabilityMax: Int?,
    val sunrise: Long,
    val sunset: Long,
    val moonrise: Long,
    val moonset: Long,
    val moonPhase: MoonPhase
)