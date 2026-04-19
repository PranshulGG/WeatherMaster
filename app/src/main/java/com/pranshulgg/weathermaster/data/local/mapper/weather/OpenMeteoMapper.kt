package com.pranshulgg.weathermaster.data.local.mapper.weather

import com.google.gson.Gson
import com.pranshulgg.weathermaster.core.model.Location
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
import com.pranshulgg.weathermaster.data.local.entity.WeatherWithRelations
import kotlin.collections.get
import kotlin.collections.map
import kotlin.uuid.ExperimentalUuidApi

private val gson = Gson()


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
        dewPoint = dewPoint
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
            precipitationProbability = item.precipitationProbability
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
        )
    }

@OptIn(ExperimentalUuidApi::class)
fun OpenMeteoWeatherDto.toDomain(location: Location): Weather =
    Weather(
        location = WeatherLocation(
            id = location.id,
            lat = location.latitude,
            lon = location.longitude,
            name = location.name,
            country = location.country,
            timezone = location.timezone,
            countryCode = location.countryCode,
            state = location.state,
            provider = location.provider,
            isFavorite = location.isFavorite,
            isPinned = location.isPinned,
            isDefault = false
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
