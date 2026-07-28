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
    isForceRefresh: Boolean
): AlertResultType {

    if (isForceRefresh) return AlertResultType.ERROR


    if (cache.isEmpty() || cache[0] == null) {
        return AlertResultType.ERROR
    }


    val cacheMilli = cache[0]!!.lastUpdatedInMilli
    val ageMillis = System.currentTimeMillis() - cacheMilli
    val ageMinutes = TimeUnit.MILLISECONDS.toMinutes(ageMillis)

    val tooEarly = isManualRefresh && ageMinutes < MANUAL_REFRESH_MINUTES
    val maxAge = if (isManualRefresh) MANUAL_REFRESH_MINUTES else AUTO_REFRESH_MAX_MINUTES

    if (tooEarly) return AlertResultType.RETURN_CACHE

    val shouldReturnCache = ageMinutes < maxAge

    return if (shouldReturnCache) AlertResultType.RETURN_CACHE else AlertResultType.ERROR
}