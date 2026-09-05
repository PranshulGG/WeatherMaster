package com.pranshulgg.weather_master_app.data.repository.alerts

import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResult
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResultType
import com.pranshulgg.weather_master_app.core.network.sources.alerts.wmosevereweather.WmoSevereWeatherApi
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnAlertsCache
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.ApiKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.entity.weather.HourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDomain
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModelResultType
import javax.inject.Inject

data class AlertCacheModel(
    val cachedAlerts: List<Alert>,
    val type: AlertCacheModelResultType,
    val apiKey: String? = null
)


enum class AlertCacheModelResultType {
    RETURN_CACHE,
    FETCH,
    NO_API_KEY_ERROR
}


class CacheResolverForAlerts @Inject constructor(
    private val dao: AlertsDao,
    private val weatherContextDao: WeatherContextDao,
    private val apiKeysDao: ApiKeysDao
) {

    suspend fun resolve(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean
    ): AlertCacheModel {
        val cache = dao.getAlertsForLocation(location.id)
        val shouldReturnCache = shouldReturnAlertsCache(
            cache,
            isManualRefresh,
            isForceRefresh,
            location.alertsLastFetchedAt
        )

        val locationRequiresApiKey = location.source.requiresUserApiKey

        val apiKey = if (locationRequiresApiKey)
            apiKeysDao.getApiKeyForSource(location.source) else null

        val domain = cache.mapNotNull { it?.toDomain() }

        if (locationRequiresApiKey && apiKey?.apiKey.isNullOrBlank()) {
            AlertCacheModel(
                cachedAlerts = domain,
                type = AlertCacheModelResultType.NO_API_KEY_ERROR,
                apiKey = apiKey?.apiKey
            )
        }


        return when (shouldReturnCache) {
            AlertResultType.RETURN_CACHE -> AlertCacheModel(
                cachedAlerts = domain,
                type = AlertCacheModelResultType.RETURN_CACHE,
                apiKey = apiKey?.apiKey
            )

            else -> AlertCacheModel(
                cachedAlerts = domain,
                type = AlertCacheModelResultType.FETCH,
                apiKey = apiKey?.apiKey
            )

        }

    }

}