package com.pranshulgg.weathermaster.data.local.entity

import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity(tableName = "weather_data")
@OptIn(ExperimentalUuidApi::class)
data class WeatherDataEntity constructor(
    val id: Uuid,

    val latitude: Double,
    val longitude: Double,

    val time: Long? = null,

    val temperature: Double? = null,

    val rain: Double? = null,
    val showers: Double? = null,
    val snowfall: Double? = null,

    val weatherCode: Int? = null,

    val windSpeed: Double? = null,

    val windDirection: Int? = null,

    val windGusts: Double? = null,

    val pressureMsl: Double? = null,

    val relativeHumidity: Double? = null,

    val isDay: Int? = null,

    val feelsLike: Double? = null,

    val cloudCover: Double? = null,

    val weatherConditionName: String? = null,

    val cachedAt: Long,

    val hourly: String,
    val daily: String

)