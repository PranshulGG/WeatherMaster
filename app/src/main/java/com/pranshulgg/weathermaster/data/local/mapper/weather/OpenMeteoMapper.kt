package com.pranshulgg.weathermaster.data.local.mapper.weather

import com.pranshulgg.weathermaster.core.model.domain.Location
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.model.domain.WeatherCurrent
import com.pranshulgg.weathermaster.core.model.domain.WeatherDaily
import com.pranshulgg.weathermaster.core.model.domain.WeatherHourly
import com.pranshulgg.weathermaster.core.network.openmeteo.OpenMeteoWeatherDto
import com.pranshulgg.weathermaster.core.network.openmeteo.openMeteoWeatherCode
import com.pranshulgg.weathermaster.core.utils.formatters.toMilliseconds
import com.pranshulgg.weathermaster.core.utils.weather.astronomy.getMoonTimings
import com.pranshulgg.weathermaster.core.utils.weather.astronomy.getSunTimings
import com.pranshulgg.weathermaster.data.local.entity.CurrentWeatherEntity
import com.pranshulgg.weathermaster.data.local.entity.DailyWeatherEntity
import com.pranshulgg.weathermaster.data.local.entity.HourlyWeatherEntity
import kotlin.uuid.ExperimentalUuidApi

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
            moonrise = item.moonrise,
            moonset = item.moonset,
            moonPhase = item.moonPhase
        )
    }

@OptIn(ExperimentalUuidApi::class)
fun OpenMeteoWeatherDto.toDomain(location: Location): Weather {

    val sunTimings = getSunTimings(
        daily.time.map { it.toMilliseconds() },
        location.timezone,
        location.latitude,
        location.longitude
    )

    val moonTimings = getMoonTimings(
        daily.time.map { it.toMilliseconds() },
        location.timezone,
        location.latitude,
        location.longitude
    )

    return Weather(
        location = Location(
            id = location.id,
            latitude = location.latitude,
            longitude = location.longitude,
            name = location.name,
            country = location.country,
            timezone = location.timezone,
            countryCode = location.countryCode,
            state = location.state,
            source = location.source,
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
            visibility = hourly.visibility[0], // TODO: Use current time index
            cloudCover = current.cloudCover,
            uvIndex = current.uvIndex,
            weatherCondition = openMeteoWeatherCode(current.weatherCode),
            feelsLike = current.feelsLike,
            time = current.time.toMilliseconds(), // Open-Meteo returns in seconds
            dewPoint = hourly.dewPoint[0], // TODO: Use current time index
            utcOffsetSeconds = utcOffsetSeconds,
            lastUpdatedInMilli = System.currentTimeMillis()
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
                time = hourly.time[it].toMilliseconds(), // Open-Meteo returns in seconds
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
                time = daily.time[it].toMilliseconds(), // Open-Meteo returns in seconds
                precipitationProbabilityMax = daily.precipitationProbabilityMax[it],
                sunrise = sunTimings[it].sunrise ?: -0L,
                sunset = sunTimings[it].sunset ?: -0L,
                moonrise = moonTimings[it].moonrise ?: -0L,
                moonset = moonTimings[it].moonset ?: -0L,
                moonPhase = moonTimings[it].phase
            )
        }
    )
}