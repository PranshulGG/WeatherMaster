package com.pranshulgg.weathermaster.core.utils.weather.cache

import com.pranshulgg.weathermaster.core.model.domain.AirQuality
import com.pranshulgg.weathermaster.core.utils.weather.cache.CacheConfig.AUTO_REFRESH_MAX_MINUTES
import com.pranshulgg.weathermaster.core.utils.weather.cache.CacheConfig.MANUAL_REFRESH_MINUTES
import com.pranshulgg.weathermaster.data.local.entity.AirQualityWithRelations
import java.util.concurrent.TimeUnit


fun isCurrentAirQualitySafe(airQuality: AirQuality): Boolean {
    val current = airQuality.current
    val isSafe =
        current.pm10 != null || current.pm25 != null || current.ozone != null || current.sulphurDioxide != null || current.nitrogenDioxide != null || current.carbonMonoxide != null
    return isSafe
}

// TODO: INCOMPLETE
fun shouldReturnAirQualityCache(
    cache: AirQualityWithRelations?,
    isManualRefresh: Boolean
): Boolean {

    if (cache == null || cache.current == null) {
        return false
    }


    val cacheMilli = cache.current.lastUpdatedSecs * 1000L
    val ageMillis = System.currentTimeMillis() - cacheMilli
    val ageMinutes = TimeUnit.MILLISECONDS.toMinutes(ageMillis)

    val tooEarly = isManualRefresh && ageMinutes < MANUAL_REFRESH_MINUTES
    val maxAge = if (isManualRefresh) MANUAL_REFRESH_MINUTES else AUTO_REFRESH_MAX_MINUTES

    if (tooEarly) return false

    val shouldReturnCache = ageMinutes < maxAge

    return shouldReturnCache
}