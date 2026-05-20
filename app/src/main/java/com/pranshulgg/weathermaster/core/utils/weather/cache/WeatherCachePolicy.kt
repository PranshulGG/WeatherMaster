package com.pranshulgg.weathermaster.core.utils.weather.cache

import com.pranshulgg.weathermaster.core.model.domain.weather.Weather
import com.pranshulgg.weathermaster.core.model.weather.WeatherResultType
import com.pranshulgg.weathermaster.core.utils.weather.cache.CacheConfig.AUTO_REFRESH_MAX_MINUTES
import com.pranshulgg.weathermaster.core.utils.weather.cache.CacheConfig.MANUAL_REFRESH_MINUTES
import com.pranshulgg.weathermaster.data.local.entity.weather.WeatherWithRelations
import java.util.concurrent.TimeUnit

fun isWeatherCacheSafe(cache: WeatherWithRelations?): Boolean {
    val isSafe = cache != null &&
            cache.daily.isNotEmpty() &&
            cache.hourly.isNotEmpty() &&
            cache.current != null

    return isSafe
}

fun isWeatherDomainSafe(weather: Weather?): Boolean {
    val isSafe = weather != null &&
            weather.daily.isNotEmpty() &&
            weather.hourly.isNotEmpty()
    return isSafe
}

fun isWeatherHourlyDomainSafe(weather: Weather?): Boolean {
    val isSafe =
        weather != null &&
                weather.hourly.isNotEmpty()

    return isSafe
}

fun isWeatherDailyDomainSafe(weather: Weather?): Boolean {
    val isSafe =
        weather != null &&
                weather.daily.isNotEmpty()

    return isSafe
}

fun shouldReturnWeatherCache(
    cache: WeatherWithRelations?,
    isManualRefresh: Boolean,
    isForceRefresh: Boolean
): WeatherResultType {

    if (isForceRefresh) return WeatherResultType.ERROR

    if (!isWeatherCacheSafe(cache)) return WeatherResultType.ERROR

    val cacheMilli = cache!!.current!!.lastUpdatedInMilli
    val ageMillis = System.currentTimeMillis() - cacheMilli
    val ageMinutes = TimeUnit.MILLISECONDS.toMinutes(ageMillis)

    val tooEarly = isManualRefresh && ageMinutes < MANUAL_REFRESH_MINUTES
    val maxAge = if (isManualRefresh) MANUAL_REFRESH_MINUTES else AUTO_REFRESH_MAX_MINUTES

    if (tooEarly) return WeatherResultType.REFRESH_TOO_EARLY

    val shouldReturnCache = ageMinutes < maxAge

    return if (shouldReturnCache) WeatherResultType.SUCCESS else WeatherResultType.ERROR
}