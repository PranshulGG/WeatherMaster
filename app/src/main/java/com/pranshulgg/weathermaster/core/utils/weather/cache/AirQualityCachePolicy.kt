package com.pranshulgg.weathermaster.core.utils.weather.cache

import com.pranshulgg.weathermaster.core.model.domain.airquality.AirQuality
import com.pranshulgg.weathermaster.core.model.weather.airquality.AirQualityResultType
import com.pranshulgg.weathermaster.core.utils.weather.cache.CacheConfig.AUTO_REFRESH_MAX_MINUTES
import com.pranshulgg.weathermaster.core.utils.weather.cache.CacheConfig.MANUAL_REFRESH_MINUTES
import com.pranshulgg.weathermaster.data.local.entity.airquality.AirQualityWithRelations
import java.util.concurrent.TimeUnit


fun isCurrentAirQualitySafe(airQuality: AirQuality?): Boolean {

    if (airQuality == null) return false

    val current = airQuality.current
    val isSafe =
        current.pm10 != null || current.pm25 != null || current.ozone != null || current.sulphurDioxide != null || current.nitrogenDioxide != null || current.carbonMonoxide != null
    return isSafe
}

fun shouldReturnAirQualityCache(
    cache: AirQualityWithRelations?,
    isManualRefresh: Boolean
): AirQualityResultType {

    if (cache == null || cache.current == null) {
        return AirQualityResultType.ERROR
    }


    val cacheMilli = cache.current.lastUpdatedInMilli
    val ageMillis = System.currentTimeMillis() - cacheMilli
    val ageMinutes = TimeUnit.MILLISECONDS.toMinutes(ageMillis)

    val tooEarly = isManualRefresh && ageMinutes < MANUAL_REFRESH_MINUTES
    val maxAge = if (isManualRefresh) MANUAL_REFRESH_MINUTES else AUTO_REFRESH_MAX_MINUTES

    if (tooEarly) return AirQualityResultType.RETURN_CACHE

    val shouldReturnCache = ageMinutes < maxAge

    return if (shouldReturnCache) AirQualityResultType.RETURN_CACHE else AirQualityResultType.ERROR
}