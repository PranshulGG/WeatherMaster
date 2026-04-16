package com.pranshulgg.weathermaster.core.model

data class Weather(
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