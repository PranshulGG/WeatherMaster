package com.pranshulgg.weather_master_app.core.utils.weather.forecast

import com.pranshulgg.weather_master_app.data.local.entity.weather.HourlyWeatherEntity


/**
 * Merges existing hourly weather with incoming hourly weather
 * Some sources do not provide past hours in the hourly forecast
 * so existing cached hours should not be removed when refreshing
 */
fun mergeHourlyWeather(
    existing: List<HourlyWeatherEntity>,
    incoming: List<HourlyWeatherEntity>
): List<HourlyWeatherEntity> {
    return (existing + incoming)
        .associateBy { it.locationId to it.time }
        .values
        .sortedBy { it.time }
}