package com.pranshulgg.weather_master_app.core.utils.weather.forecast

import com.pranshulgg.weather_master_app.data.local.entity.weather.HourlyWeatherEntity
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours


/**
 * Merges existing hourly weather with incoming hourly weather
 * Some sources do not provide past hours in the hourly forecast
 * so existing cached hours should not be removed when refreshing
 */
fun mergeHourlyWeather(
    existing: List<HourlyWeatherEntity>,
    incoming: List<HourlyWeatherEntity>
): List<HourlyWeatherEntity> {

    // Only keep the last 24 hours
    val cutoff = Clock.System.now().minus(24.hours).toEpochMilliseconds()

    return (existing + incoming)
        .filter { it.time >= cutoff }
        .associateBy { it.locationId to it.time }
        .values
        .sortedBy { it.time }
}