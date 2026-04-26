package com.pranshulgg.weathermaster.data.local.mapper

import com.pranshulgg.weathermaster.core.model.WeatherConditions
import com.pranshulgg.weathermaster.core.model.domain.Location
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.model.domain.WeatherCurrent
import com.pranshulgg.weathermaster.core.model.domain.WeatherDaily
import com.pranshulgg.weathermaster.core.model.domain.WeatherHourly
import com.pranshulgg.weathermaster.data.local.entity.DailyWeatherEntity
import com.pranshulgg.weathermaster.data.local.entity.WeatherWithRelations
import java.time.Instant
import java.time.ZoneId
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun WeatherWithRelations.toDomain(): Weather {
    val timezone = location.timezone


    val todayIndex = getDailyIndexForToday(
        current?.time ?: Instant.now().epochSecond,
        daily,
        timezone
    ).coerceAtLeast(0)

    return Weather(
        location = Location(
            id = location.id,
            latitude = location.lat,
            longitude = location.lon,
            name = location.name,
            country = location.country,
            timezone = location.timezone,
            countryCode = location.countryCode,
            state = location.state ?: "",
            provider = location.provider,
            isFavorite = location.isFavorite,
            isPinned = location.isPinned,
            isDefault = location.isDefault
        ),
        current = WeatherCurrent(
            temperature = current?.temperature ?: 0.0,
            humidity = current?.humidity ?: 0.0,
            windSpeed = current?.windSpeed ?: 0.0,
            windDirection = current?.windDirection ?: 0,
            pressureMsl = current?.pressureMsl ?: 0.0,
            visibility = current?.visibility ?: 0,
            cloudCover = current?.cloudCover ?: 0.0,
            uvIndex = current?.uvIndex ?: 0.0,
            weatherCondition = current?.weatherCondition ?: WeatherConditions.NO_CONDITION_FOUND,
            feelsLike = current?.feelsLike ?: 0.0,
            time = current?.time ?: System.currentTimeMillis(),
            dewPoint = current?.dewPoint,
            utcOffsetSeconds = current?.utcOffsetSeconds ?: -1L,
            lastUpdatedSecs = current?.lastUpdatedSecs ?: -1L
        ),
        hourly = List(hourly.size) {
            WeatherHourly(
                temperature = hourly[it].temperature,
                windSpeed = hourly[it].windSpeed,
                windDirection = hourly[it].windDirection,
                rain = hourly[it].rain,
                snowfall = hourly[it].snowfall,
                uvIndex = hourly[it].uvIndex,
                weatherCondition = hourly[it].weatherCondition,
                time = hourly[it].time,
                precipitationProbability = hourly[it].precipitationProbability,
            )
        },
        daily = List(daily.size) {
            WeatherDaily(
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
                sunset = daily[it].sunset
            )

        }.drop(todayIndex)
    )
}

private fun getDailyIndexForToday(
    targetTimeSecs: Long,
    dailyList: List<DailyWeatherEntity>,
    timezone: String
): Int {

    val zoneId = ZoneId.of(timezone)

    val targetDate = Instant.ofEpochSecond(targetTimeSecs)
        .atZone(zoneId)
        .toLocalDate()

    return dailyList.indexOfFirst { daily ->
        Instant.ofEpochSecond(daily.time)
            .atZone(zoneId)
            .toLocalDate() == targetDate
    }.coerceAtLeast(0)

}