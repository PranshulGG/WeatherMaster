package com.pranshulgg.weather_master_app.core.network.sources.alerts.weatherapi

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResult
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResultType
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnAlertsCache
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.sources.weatherapi.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.toEntity
import com.pranshulgg.weather_master_app.data.repository.data.AlertRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class AlertsWeatherApiRepository @Inject constructor(
    private val api: AlertsWeatherApi,
    private val dao: AlertsDao,
    private val locationsDao: LocationsDao
) : AlertRepository {

    override val alertSource = Source.WEATHER_API

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

        return@withContext try {

            val response = api.fetchAlerts("${location.latitude},${location.longitude}")
            val body = response.body()
                ?: return@withContext AlertResult.Error(exception = AppException.Unknown())

            val domain = body.toDomain(location.id)

            dao.insertAlerts(
                domain.map { it.toEntity(location.id) },
                location.id
            )
            locationsDao.updateAlertsLastFetchedAt(location.id, System.currentTimeMillis())

            AlertResult.Success(domain)

        } catch (e: Exception) {
            AlertResult.Error(exception = e, cacheAlerts = cache.map { it!!.toDomain() })
        }


    }
}