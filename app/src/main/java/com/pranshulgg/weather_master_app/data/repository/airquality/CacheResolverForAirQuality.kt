package com.pranshulgg.weather_master_app.data.repository.airquality

import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityResultType
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResultType
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnAirQualityCache
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnAlertsCache
import com.pranshulgg.weather_master_app.data.local.dao.airquality.AirQualityDao
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.ApiKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.mapper.airquality.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.toDomain
import javax.inject.Inject


data class AirQualityCacheModel(
    val cachedAirQuality: AirQuality?,
    val type: AirQualityCacheModelResultType,
    val apiKey: String? = null
)


enum class AirQualityCacheModelResultType {
    RETURN_CACHE,
    FETCH,
    NO_API_KEY_ERROR
}


class CacheResolverForAirQuality @Inject constructor(
    private val dao: AirQualityDao,
    private val apiKeysDao: ApiKeysDao
) {

    suspend fun resolve(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean
    ): AirQualityCacheModel {

        val cache = dao.getAirQualityForLocation(location.id)
        val shouldReturnCache = shouldReturnAirQualityCache(cache, isManualRefresh, isForceRefresh)

        val locationRequiresApiKey = location.source.requiresUserApiKey

        val apiKey = if (locationRequiresApiKey)
            apiKeysDao.getApiKeyForSource(location.source) else null

        val domain = cache.toDomain()

        if (locationRequiresApiKey && apiKey?.apiKey.isNullOrBlank()) {
            AirQualityCacheModel(
                cachedAirQuality = domain,
                type = AirQualityCacheModelResultType.NO_API_KEY_ERROR,
                apiKey = apiKey?.apiKey
            )
        }


        return when (shouldReturnCache) {
            AirQualityResultType.RETURN_CACHE -> AirQualityCacheModel(
                cachedAirQuality = domain,
                type = AirQualityCacheModelResultType.RETURN_CACHE,
                apiKey = apiKey?.apiKey
            )

            else -> AirQualityCacheModel(
                cachedAirQuality = domain,
                type = AirQualityCacheModelResultType.FETCH,
                apiKey = apiKey?.apiKey
            )

        }

    }

}