package com.pranshulgg.weathermaster.core.model

import com.google.gson.annotations.SerializedName
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class Weather(
    val location: WeatherLocation,
    val current: WeatherCurrent,
    val hourly: List<WeatherHourly>,
    val daily: List<WeatherDaily>
)

@OptIn(ExperimentalUuidApi::class)
data class WeatherLocation(
    val id: String,
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val timezone: String
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
    val time: Long?,
    val dewPoint: Double?
)


data class WeatherHourly(
    val temperature: Double,
    val windSpeed: Double,
    val windDirection: Int?,
    val rain: Double,
    val snowfall: Double?,
    val uvIndex: Double,
    val weatherCondition: WeatherConditions,
    val time: Long?,
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
    val time: Long?,
    val precipitationProbabilityMax: Int?,
    val sunrise: Long?,
    val sunset: Long?
)