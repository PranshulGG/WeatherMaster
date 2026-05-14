package com.pranshulgg.weathermaster.core.model.domain

import com.pranshulgg.weathermaster.core.model.astro.MoonPhase
import com.pranshulgg.weathermaster.core.model.weather.WeatherConditions

data class Weather(
    val location: Location,
    val current: WeatherCurrent,
    val hourly: List<WeatherHourly>,
    val daily: List<WeatherDaily>
)

data class WeatherCurrent(
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
    val lastUpdatedInMilli: Long
)

data class WeatherHourly(
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

data class WeatherDaily(
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