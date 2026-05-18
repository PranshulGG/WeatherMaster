package com.pranshulgg.weathermaster.data.local.mapper.weather.sources.openmeteo

import com.pranshulgg.weathermaster.core.model.domain.location.Location
import com.pranshulgg.weathermaster.core.model.domain.weather.Weather
import com.pranshulgg.weathermaster.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weathermaster.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weathermaster.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weathermaster.core.model.weather.wind.WindDirection
import com.pranshulgg.weathermaster.core.network.sources.weather.openmeteo.json.OpenMeteoWeatherJson
import com.pranshulgg.weathermaster.core.network.sources.weather.openmeteo.openMeteoWeatherCode
import com.pranshulgg.weathermaster.core.utils.formatters.toMilliseconds
import com.pranshulgg.weathermaster.core.utils.weather.astronomy.getMoonTimings
import com.pranshulgg.weathermaster.core.utils.weather.astronomy.getSunTimings
import kotlin.uuid.ExperimentalUuidApi

// ---------------------------- JSON TO DOMAIN ----------------------------

@OptIn(ExperimentalUuidApi::class)
fun OpenMeteoWeatherJson.toDomain(location: Location): Weather {

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
            windDirection = WindDirection.toWindDirectionFromDegrees(current.windDirection),
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
                windDirection = WindDirection.toWindDirectionFromDegrees(hourly.windDirection[it]),
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
                windDirection = WindDirection.toWindDirectionFromDegrees(daily.windDirectionDominant[it]),
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