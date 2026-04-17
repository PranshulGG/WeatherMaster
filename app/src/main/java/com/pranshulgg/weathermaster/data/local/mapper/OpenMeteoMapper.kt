package com.pranshulgg.weathermaster.data.local.mapper

import com.google.gson.Gson
import com.pranshulgg.weathermaster.core.model.Weather
import com.pranshulgg.weathermaster.core.model.WeatherCurrent
import com.pranshulgg.weathermaster.core.model.WeatherDaily
import com.pranshulgg.weathermaster.core.model.WeatherHourly
import com.pranshulgg.weathermaster.core.model.WeatherLocation
import com.pranshulgg.weathermaster.core.network.openmeteo.OpenMeteoWeatherDto
import com.pranshulgg.weathermaster.core.network.openmeteo.openMeteoWeatherCode
import com.pranshulgg.weathermaster.core.utils.UuidGenerator
import com.pranshulgg.weathermaster.data.local.entity.CurrentWeatherEntity
import com.pranshulgg.weathermaster.data.local.entity.DailyWeatherEntity
import com.pranshulgg.weathermaster.data.local.entity.HourlyWeatherEntity
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private val gson = Gson()

@OptIn(ExperimentalUuidApi::class)
fun Weather.toCurrentWeatherEntity(): CurrentWeatherEntity =
    CurrentWeatherEntity(
        locationId = location.id,
        temperature = current.temperature,
        humidity = current.humidity,
        windSpeed = current.windSpeed,
        windDirection = current.windDirection,
        pressureMsl = current.pressureMsl,
        visibility = current.visibility,
        cloudCover = current.cloudCover,
        uvIndex = current.uvIndex,
        weatherCondition = current.weatherCondition,
        feelsLike = current.feelsLike,
        time = current.time,
        dewPoint = current.dewPoint
    )

@OptIn(ExperimentalUuidApi::class)
fun Weather.toHourlyWeatherEntity(): List<HourlyWeatherEntity> =
    List(hourly.size) {
        HourlyWeatherEntity(
            locationId = location.id,
            temperature = hourly[it].temperature,
            windSpeed = hourly[it].windSpeed,
            windDirection = hourly[it].windDirection,
            rain = hourly[it].rain,
            snowfall = hourly[it].snowfall,
            uvIndex = hourly[it].uvIndex,
            weatherCondition = hourly[it].weatherCondition,
            time = hourly[it].time,
            precipitationProbability = hourly[it].precipitationProbability
        )
    }

@OptIn(ExperimentalUuidApi::class)
fun Weather.toDailyWeatherEntity(): List<DailyWeatherEntity> =
    List(daily.size) {
        DailyWeatherEntity(
            locationId = location.id,
            temperatureMin = daily[it].temperatureMin,
            temperatureMax = daily[it].temperatureMax,
            windSpeed = daily[it].windSpeed,
            windDirection = daily[it].windDirection,
            rainSum = daily[it].rainSum,
            snowfallSum = daily[it].snowfallSum,
            uvIndexMax = daily[it].uvIndexMax,
            weatherCondition = daily[it].weatherCondition,
            time = daily[it].time,
            precipitationProbabilityMax = daily[it].precipitationProbabilityMax,
            sunrise = daily[it].sunrise,
            sunset = daily[it].sunset,
        )
    }

@OptIn(ExperimentalUuidApi::class)
fun OpenMeteoWeatherDto.toDomain(): Weather =
    Weather(
        location = WeatherLocation(
            id = UuidGenerator().generateId(),
            lat = latitude,
            lon = longitude,
            name = "",
            country = "",
            timezone = ""
        ),
        current = WeatherCurrent(
            temperature = current.temperature,
            humidity = current.relativeHumidity,
            windSpeed = current.windSpeed,
            windDirection = current.windDirection,
            pressureMsl = current.pressureMsl,
            visibility = hourly.visibility[0],
            cloudCover = current.cloudCover,
            uvIndex = current.uvIndex,
            weatherCondition = openMeteoWeatherCode(current.weatherCode),
            feelsLike = current.feelsLike,
            time = current.time,
            dewPoint = hourly.dewPoint[0]
        ),
        hourly = List(hourly.time.size) {
            WeatherHourly(
                temperature = hourly.temperature[it],
                windSpeed = hourly.windSpeed[it],
                windDirection = hourly.windDirection[it],
                rain = hourly.rain[it],
                snowfall = hourly.snowfall[it],
                uvIndex = hourly.uvIndex[it],
                weatherCondition = openMeteoWeatherCode(hourly.weatherCode[it]),
                time = hourly.time[it],
                precipitationProbability = hourly.precipitationProbability[it],
            )
        },
        daily = List(daily.time.size) {
            WeatherDaily(
                temperatureMin = daily.temperatureMin[it],
                temperatureMax = daily.temperatureMax[it],
                windSpeed = daily.windSpeedMax[it],
                windDirection = daily.windDirectionDominant[it],
                rainSum = daily.rainSum[it],
                snowfallSum = daily.snowfallSum[it],
                uvIndexMax = daily.uvIndexMax[it],
                weatherCondition = openMeteoWeatherCode(daily.weatherCode[it]),
                time = daily.time[it],
                precipitationProbabilityMax = daily.precipitationProbabilityMax[it],
                sunrise = daily.sunrise[it],
                sunset = daily.sunset[it]
            )
        }
    )
