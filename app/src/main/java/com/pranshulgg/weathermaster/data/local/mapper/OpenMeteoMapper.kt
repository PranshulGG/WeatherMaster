package com.pranshulgg.weathermaster.data.local.mapper

import com.google.gson.Gson
import com.pranshulgg.weathermaster.core.model.Weather
import com.pranshulgg.weathermaster.core.network.openmeteo.OpenMeteoWeatherDto
import com.pranshulgg.weathermaster.data.local.entity.WeatherDataEntity
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private val gson = Gson()

@OptIn(ExperimentalUuidApi::class)
fun OpenMeteoWeatherDto.toEntity(): WeatherDataEntity =
    WeatherDataEntity(
        id = Uuid.random(),
        latitude = latitude,
        longitude = longitude,
        time = current.time,
        temperature = current.temperature,
        rain = current.rain,
        showers = current.showers,
        snowfall = current.snowfall,
        weatherCode = current.weatherCode,
        windSpeed = current.windSpeed,
        windDirection = current.windDirection,
        windGusts = current.windGusts,
        pressureMsl = current.pressureMsl,
        relativeHumidity = current.relativeHumidity,
        isDay = current.isDay,
        feelsLike = current.feelsLike,
        cloudCover = current.cloudCover,
        weatherConditionName = null,
        cachedAt = System.currentTimeMillis(),
        hourly = gson.toJson(hourly),
        daily = gson.toJson(gson.toJson(hourly)),
    )


@OptIn(ExperimentalUuidApi::class)
fun OpenMeteoWeatherDto.toDomain(): Weather =
    Weather(
        latitude = latitude,
        longitude = longitude,
        time = current.time,
        temperature = current.temperature,
        rain = current.rain,
        showers = current.showers,
        snowfall = current.snowfall,
        weatherCode = current.weatherCode,
        windSpeed = current.windSpeed,
        windDirection = current.windDirection,
        windGusts = current.windGusts,
        pressureMsl = current.pressureMsl,
        relativeHumidity = current.relativeHumidity,
        isDay = current.isDay,
        feelsLike = current.feelsLike,
        cloudCover = current.cloudCover,
        weatherConditionName = null,
        cachedAt = System.currentTimeMillis(),
        hourly = gson.toJson(hourly),
        daily = gson.toJson(gson.toJson(hourly)),
    )
