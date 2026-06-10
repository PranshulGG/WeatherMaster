package com.pranshulgg.weather_master_app.data.local.mapper.weather

import android.util.Log
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.utils.formatters.safeZoneId
import com.pranshulgg.weather_master_app.data.local.entity.weather.DailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.entity.weather.WeatherWithRelations
import java.time.Instant
import java.time.ZoneId
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun WeatherWithRelations.toDomain(): Weather {
    val timezone = location.timezone

    // DROP PAST DAYS
    val todayIndex = getDailyIndexForToday(
        current?.time ?: System.currentTimeMillis(),
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
            source = location.source,
            isFavorite = location.isFavorite,
            isPinned = location.isPinned,
            isDefault = location.isDefault
        ),
        current = WeatherCurrent(
            temperature = current?.temperature,
            humidity = current?.humidity ?: 0.0,
            windSpeed = current?.windSpeed,
            windDirection = current?.windDirection,
            pressureMsl = current?.pressureMsl,
            visibility = current?.visibility,
            cloudCover = current?.cloudCover,
            uvIndex = current?.uvIndex,
            weatherCondition = current?.weatherCondition ?: WeatherCondition.NO_CONDITION_FOUND,
            feelsLike = current?.feelsLike,
            time = current?.time ?: System.currentTimeMillis(),
            dewPoint = current?.dewPoint,
            utcOffsetSeconds = current?.utcOffsetSeconds,
            lastUpdatedInMilli = current?.lastUpdatedInMilli ?: -1L
        ),
        hourly = List(hourly.size) {
            WeatherHourly(
                temperature = hourly[it].temperature,
                windSpeed = hourly[it].windSpeed,
                windDirection = hourly[it].windDirection,
                rain = hourly[it].rain,
                snowfall = hourly[it].snowfall ?: 0.0,
                uvIndex = hourly[it].uvIndex,
                weatherCondition = hourly[it].weatherCondition,
                time = hourly[it].time,
                precipitationProbability = hourly[it].precipitationProbability,
                pressureMsl = hourly[it].pressureMsl,
                humidity = hourly[it].humidity,
                visibility = hourly[it].visibility,
                dewPoint = hourly[it].dewPoint
            )
        },
        daily = List(daily.size) {
            WeatherDaily(
                temperatureMin = daily[it].temperatureMin,
                temperatureMax = daily[it].temperatureMax,
                windSpeed = daily[it].windSpeed,
                windDirection = daily[it].windDirection,
                rainSum = daily[it].rainSum,
                snowfallSum = daily[it].snowfallSum ?: 0.0,
                uvIndexMax = daily[it].uvIndexMax,
                weatherCondition = daily[it].weatherCondition,
                time = daily[it].time,
                precipitationProbabilityMax = daily[it].precipitationProbabilityMax,
                sunrise = daily[it].sunrise,
                sunset = daily[it].sunset,
                moonrise = daily[it].moonrise,
                moonset = daily[it].moonset,
                moonPhase = daily[it].moonPhase,
                dusk = daily[it].dusk,
                dawn = daily[it].dawn
            )

        }.drop(todayIndex)
    )
}

private fun getDailyIndexForToday(
    targetTimeMillis: Long,
    dailyList: List<DailyWeatherEntity>,
    timezone: String
): Int {

    val zoneId = safeZoneId(timezone)


    val targetDate = Instant.ofEpochMilli(targetTimeMillis)
        .atZone(zoneId)
        .toLocalDate()

    return dailyList.indexOfFirst { daily ->
        Instant.ofEpochSecond(daily.time)
            .atZone(zoneId)
            .toLocalDate() == targetDate
    }.coerceAtLeast(0)

}