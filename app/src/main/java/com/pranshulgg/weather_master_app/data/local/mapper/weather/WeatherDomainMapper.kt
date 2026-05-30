package com.pranshulgg.weather_master_app.data.local.mapper.weather

import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.data.local.entity.weather.CurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.entity.weather.DailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.entity.weather.HourlyWeatherEntity
import kotlin.uuid.ExperimentalUuidApi

// ---------------------------- DOMAIN TO ENTITY ----------------------------

// CURRENT
@OptIn(ExperimentalUuidApi::class)
fun WeatherCurrent.toCurrentWeatherEntity(
    locationId: String
): CurrentWeatherEntity =
    CurrentWeatherEntity(
        locationId = locationId,
        temperature = temperature,
        humidity = humidity,
        windSpeed = windSpeed,
        windDirection = windDirection,
        pressureMsl = pressureMsl,
        visibility = visibility,
        cloudCover = cloudCover,
        uvIndex = uvIndex,
        weatherCondition = weatherCondition,
        feelsLike = feelsLike,
        time = time,
        dewPoint = dewPoint,
        utcOffsetSeconds = utcOffsetSeconds,
        lastUpdatedInMilli = lastUpdatedInMilli
    )


@OptIn(ExperimentalUuidApi::class)
fun List<WeatherHourly>.toHourlyWeatherEntity(
    locationId: String
): List<HourlyWeatherEntity> =
    map { item ->
        HourlyWeatherEntity(
            locationId = locationId,
            temperature = item.temperature,
            windSpeed = item.windSpeed,
            windDirection = item.windDirection,
            rain = item.rain,
            snowfall = item.snowfall,
            uvIndex = item.uvIndex,
            weatherCondition = item.weatherCondition,
            time = item.time,
            precipitationProbability = item.precipitationProbability,
            visibility = item.visibility,
            pressureMsl = item.pressureMsl,
            humidity = item.humidity,
            dewPoint = item.dewPoint
        )
    }


@OptIn(ExperimentalUuidApi::class)
fun List<WeatherDaily>.toDailyWeatherEntity(
    locationId: String
): List<DailyWeatherEntity> =
    map { item ->
        DailyWeatherEntity(
            locationId = locationId,
            temperatureMin = item.temperatureMin,
            temperatureMax = item.temperatureMax,
            windSpeed = item.windSpeed,
            windDirection = item.windDirection,
            rainSum = item.rainSum,
            snowfallSum = item.snowfallSum,
            uvIndexMax = item.uvIndexMax,
            weatherCondition = item.weatherCondition,
            time = item.time,
            precipitationProbabilityMax = item.precipitationProbabilityMax,
            sunrise = item.sunrise,
            sunset = item.sunset,
            moonrise = item.moonrise,
            moonset = item.moonset,
            moonPhase = item.moonPhase,
            dawn = item.dawn,
            dusk = item.dusk
        )
    }


