package com.pranshulgg.weathermaster.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.pranshulgg.weathermaster.core.model.WeatherConditions
import com.pranshulgg.weathermaster.core.utils.UuidGenerator
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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

//    @PrimaryKey
//    val id: String = UuidGenerator().generateId(),

    val temperature: Double,
    val windSpeed: Double,
    val windDirection: Int?,
    val rain: Double,
    val snowfall: Double?,
    val uvIndex: Double,
    val weatherCondition: WeatherConditions,
    val time: Long,
    val precipitationProbability: Int?
)