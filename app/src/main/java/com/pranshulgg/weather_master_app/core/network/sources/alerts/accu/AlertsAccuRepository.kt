package com.pranshulgg.weather_master_app.core.network.sources.alerts.accu

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResult
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResultType
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.AccuApi
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnAlertsCache
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationKeysDao
import com.pranshulgg.weather_master_app.data.local.entity.location.LocationKeyEntity
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.sources.accu.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.toEntity
import com.pranshulgg.weather_master_app.data.local.mapper.locations.toDomain
import com.pranshulgg.weather_master_app.data.repository.AlertRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class AlertsAccuRepository @Inject constructor(
    private val api: AlertsAccuApi,
    private val dao: AlertsDao,
    private val locationKeysDao: LocationKeysDao,
    private val accuApi: AccuApi,
) : AlertRepository {
    override suspend fun getAlerts(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean
    ): AlertResult = withContext(Dispatchers.IO) {

        val cache = dao.getAlertsForLocation(location.id)
        val shouldReturnCache = shouldReturnAlertsCache(cache, isManualRefresh, isForceRefresh)

        when (shouldReturnCache) {
            AlertResultType.RETURN_CACHE -> return@withContext AlertResult.Success(cache.map { it!!.toDomain() })
            else -> {}
        }

        return@withContext try {
            val locationKey =
                locationKeysDao.getCityKeyForLocation(location.id)?.toDomain()?.cityKey
                    ?: accuApi.getLocationKey("${location.latitude},${location.longitude}")
                        .body()?.key
                    ?: return@withContext AlertResult.Error(exception = AppException.Unknown())

            val response = api.fetchAlerts(locationKey)
            val body = response.body()
                ?: return@withContext AlertResult.Error(exception = AppException.Unknown())

            val domain = body.map { it.toDomain(location.id) }

            locationKeysDao.insertCityKey(
                LocationKeyEntity(
                    locationId = location.id,
                    cityKey = locationKey
                )
            )
            dao.insertAlerts(
                domain.map { it.toEntity(location.id) },
                location.id
            )

            AlertResult.Success(domain)

        } catch (e: Exception) {
            AlertResult.Error(exception = e, cacheAlerts = cache.map { it!!.toDomain() })
        }


    }
}