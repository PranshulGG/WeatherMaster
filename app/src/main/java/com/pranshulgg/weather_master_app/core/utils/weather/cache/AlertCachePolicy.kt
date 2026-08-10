package com.pranshulgg.weather_master_app.core.utils.weather.cache

import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityResultType
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResultType
import com.pranshulgg.weather_master_app.core.utils.weather.cache.CacheConfig.AUTO_REFRESH_MAX_MINUTES
import com.pranshulgg.weather_master_app.core.utils.weather.cache.CacheConfig.MANUAL_REFRESH_MINUTES
import com.pranshulgg.weather_master_app.data.local.entity.airquality.AirQualityWithRelations
import com.pranshulgg.weather_master_app.data.local.entity.alerts.AlertEntity
import java.util.concurrent.TimeUnit


fun shouldReturnAlertsCache(
    cache: List<AlertEntity?>,
    isManualRefresh: Boolean,
    isForceRefresh: Boolean,
    alertsLastFetchedAt: Long?
): AlertResultType {

    if (isForceRefresh) return AlertResultType.ERROR

    val cacheTimestamp = alertsLastFetchedAt
        ?: cache.firstOrNull()?.lastUpdatedInMilli
        ?: return AlertResultType.ERROR

    val ageMillis = System.currentTimeMillis() - cacheTimestamp
    val ageMinutes = TimeUnit.MILLISECONDS.toMinutes(ageMillis)

    val tooEarly = isManualRefresh && ageMinutes < MANUAL_REFRESH_MINUTES

    if (tooEarly) {
        return AlertResultType.RETURN_CACHE
    }

    val maxAge = if (isManualRefresh) {
        MANUAL_REFRESH_MINUTES
    } else {
        AUTO_REFRESH_MAX_MINUTES
    }

    return if (ageMinutes < maxAge) {
        AlertResultType.RETURN_CACHE
    } else {
        AlertResultType.ERROR
    }
}