package com.pranshulgg.weather_master_app.data.local.entity.weather

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.pranshulgg.weather_master_app.core.model.astro.MoonPhase
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.data.local.entity.location.WeatherLocationEntity

@Entity(
    tableName = "weather_daily",
    foreignKeys = [
        ForeignKey(
            entity = WeatherLocationEntity::class,
            parentColumns = ["id"],
            childColumns = ["locationId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [Index("locationId")],
    primaryKeys = ["locationId", "time"]
)
data class DailyWeatherEntity(
    val locationId: String,

    val temperatureMin: Double,
    val temperatureMax: Double,
    val windSpeed: Double?,
    val windDirection: WindDirection?,
    val rainSum: Double,
    val snowfallSum: Double?,
    val uvIndexMax: Double?,
    val weatherCondition: WeatherCondition,
    val time: Long,
    val precipitationProbabilityMax: Int?,
    val sunrise: Long,
    val sunset: Long,
    val moonrise: Long,
    val moonset: Long,
    val moonPhase: MoonPhase
)