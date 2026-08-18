package com.pranshulgg.weather_master_app.core.network.sources.alerts.pirateweather

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResult
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResultType
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnAlertsCache
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.ApiKeysDao
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.sources.pirateweather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.toEntity
import com.pranshulgg.weather_master_app.data.repository.AlertRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PirateWeatherAlertsRepository @Inject constructor(
    private val api: PirateWeatherAlertsApi,
    private val dao: AlertsDao,
    private val locationsDao: LocationsDao,
    private val apiKeysDao: ApiKeysDao
) : AlertRepository {

    override suspend fun getAlerts(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean
    ): AlertResult = withContext(Dispatchers.IO) {

        val cache = dao.getAlertsForLocation(location.id)
        val shouldReturnCache = shouldReturnAlertsCache(
            cache,
            isManualRefresh,
            isForceRefresh,
            location.alertsLastFetchedAt
        )

        when (shouldReturnCache) {
            AlertResultType.RETURN_CACHE -> return@withContext AlertResult.Success(cache.map { it!!.toDomain() })
            else -> {}
        }

        val apiKey = apiKeysDao.getApiKeyForSource(WeatherSource.PIRATE_WEATHER)
        if (apiKey?.apiKey.isNullOrBlank()) {
            return@withContext AlertResult.Error(
                exception = AppException.NoApiKeyError(),
                cacheAlerts = cache.map { it!!.toDomain() }
            )
        }

        return@withContext try {
            val response = safeApiCall {
                api.fetchAlerts(apiKey.apiKey, "${location.latitude},${location.longitude}")
            }.getOrElse {
                return@withContext AlertResult.Error(
                    exception = it.toAppException(),
                    cacheAlerts = cache.map { it2 -> it2!!.toDomain() }
                )
            }

            val domain = (response.alerts ?: emptyList()).toDomain(location.id)

            dao.insertAlerts(
                domain.map { it.toEntity(location.id) },
                location.id
            )
            locationsDao.updateAlertsLastFetchedAt(location.id, System.currentTimeMillis())

            AlertResult.Success(domain)
        } catch (e: Exception) {
            AlertResult.Error(
                exception = e,
                cacheAlerts = cache.map { it!!.toDomain() }
            )
        }
    }
}
