package com.pranshulgg.weather_master_app.data.repository.data

import android.util.Log
import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.weather.WeatherDataPack
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityDataPack
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityResult
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResult
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertsDataPack
import com.pranshulgg.weather_master_app.core.model.weather.nws.NwsGridPoints
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.mergeHourlyWeather
import com.pranshulgg.weather_master_app.data.local.dao.airquality.AirQualityDao
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.entity.weather.HourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.airquality.toEntity
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.toEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.repository.airquality.AirQualityCacheModel
import com.pranshulgg.weather_master_app.data.repository.airquality.AirQualityRepository
import com.pranshulgg.weather_master_app.data.repository.alerts.AlertCacheModel
import com.pranshulgg.weather_master_app.data.repository.alerts.AlertCacheModelResultType
import com.pranshulgg.weather_master_app.data.repository.alerts.AlertRepository
import com.pranshulgg.weather_master_app.data.repository.capability.AirQualityCapability
import com.pranshulgg.weather_master_app.data.repository.capability.AlertCapability
import com.pranshulgg.weather_master_app.data.repository.capability.WeatherCapability
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModelResultType
import com.pranshulgg.weather_master_app.data.repository.weather.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.pranshulgg.weather_master_app.data.repository.airquality.AirQualityCacheModelResultType


data class WeatherAdditionalData(
    val alerts: AlertsDataPack? = null,
    val airQuality: AirQuality? = null,
    val locationKey: String? = null,
    val nwsGridPoints: NwsGridPoints? = null
)

data class AlertsAdditionalData(
    val locationKey: String? = null
)

data class AirQualityAdditionalData(
    val locationKey: String? = null
)


abstract class BaseRepository : WeatherRepository, AlertRepository, AirQualityRepository {

    protected abstract fun weatherCapability(): WeatherCapability?
    protected abstract fun alertCapability(): AlertCapability?

    protected abstract fun airQualityCapability(): AirQualityCapability?

    final override suspend fun getWeather(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean,
        cacheModel: CacheModel
    ): WeatherResult = withContext(Dispatchers.IO) {

        val capability = weatherCapability()
            ?: return@withContext WeatherResult.NotSupported()

        val cache = cacheModel.cachedWeather


        val data = try {

            if (cacheModel.type == CacheModelResultType.NO_API_KEY_ERROR) {
                return@withContext WeatherResult.Error(
                    exception = AppException.NoApiKeyError(),
                    weather = cache
                )
            }

            if (cacheModel.type == CacheModelResultType.FETCH || cache == null) {
                capability.fetchAndProcess(
                    location,
                    isManualRefresh,
                    isForceRefresh,
                    cacheModel
                )
            } else if (cacheModel.type == CacheModelResultType.REFRESH_TOO_EARLY) {
                return@withContext WeatherResult.RefreshNotAvailable(weather = cache)
            } else WeatherDataPack(weather = cache)

        } catch (e: Exception) {
            return@withContext WeatherResult.Error(
                exception = e.toAppException(),
                weather = cache
            )
        }

        if (cacheModel.type == CacheModelResultType.FETCH || cache == null) {
            try {
                capability.saveAdditionalDataToDb(pack = data)
                capability.saveToDb(data, cacheModel)
            } catch (e: Exception) {
                return@withContext WeatherResult.Error(
                    exception = e.toAppException(),
                    weather = cache
                )
            }
        }

        val finished = capability.finishedResult(data.weather)

        WeatherResult.Success(weather = finished.weather)
    }

    final override suspend fun getAlerts(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean,
        alertCacheModel: AlertCacheModel
    ): AlertResult =
        withContext(Dispatchers.IO) {

            val capability = alertCapability() ?: return@withContext AlertResult.NotSupported()

            val cache = alertCacheModel.cachedAlerts

            val data = try {

                if (alertCacheModel.type == AlertCacheModelResultType.NO_API_KEY_ERROR) {
                    return@withContext AlertResult.Error(
                        exception = AppException.NoApiKeyError(),
                        alerts = cache
                    )
                }

                if (alertCacheModel.type == AlertCacheModelResultType.FETCH) {
                    capability.fetchAndProcess(
                        location,
                        isManualRefresh,
                        isForceRefresh,
                        alertCacheModel
                    )
                } else AlertsDataPack(alerts = cache, location = location)

            } catch (e: Exception) {
                return@withContext AlertResult.Error(
                    exception = e.toAppException(),
                    alerts = cache
                )
            }

            if (alertCacheModel.type == AlertCacheModelResultType.FETCH) {
                try {
                    capability.saveAdditionalDataToDb(pack = data)
                    capability.saveToDb(data, alertCacheModel)
                } catch (e: Exception) {
                    return@withContext AlertResult.Error(
                        exception = e.toAppException(),
                        alerts = cache
                    )
                }
            }

            val finished = capability.finishedResult(data.alerts)

            AlertResult.Success(alerts = finished.alerts)
        }


    final override suspend fun getAirQuality(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean,
        airQualityCacheModel: AirQualityCacheModel
    ): AirQualityResult = withContext(Dispatchers.IO) {
        val capability =
            airQualityCapability() ?: return@withContext AirQualityResult.NotSupported()

        val cache = airQualityCacheModel.cachedAirQuality

        val data = try {
            if (airQualityCacheModel.type == AirQualityCacheModelResultType.NO_API_KEY_ERROR) {
                return@withContext AirQualityResult.Error(
                    exception = AppException.NoApiKeyError(),
                    airQuality = cache
                )
            }

            if (airQualityCacheModel.type == AirQualityCacheModelResultType.FETCH) {
                capability.fetchAndProcess(
                    location,
                    isManualRefresh,
                    isForceRefresh,
                    airQualityCacheModel
                )
            } else AirQualityDataPack(airQuality = cache, location = location)

        } catch (e: Exception) {
            return@withContext AirQualityResult.Error(
                exception = e.toAppException(),
                airQuality = cache
            )
        }

        if (airQualityCacheModel.type == AirQualityCacheModelResultType.FETCH) {
            try {
                capability.saveAdditionalDataToDb(pack = data)
                capability.saveToDb(data)
            } catch (e: Exception) {
                return@withContext AirQualityResult.Error(
                    exception = e.toAppException(),
                    airQuality = cache
                )
            }
        }

        val finished = capability.finishedResult(data.airQuality!!)

        AirQualityResult.Success(airQuality = finished.airQuality)
    }

    suspend fun useGenericSaveImplementationForWeather(
        existingHourly: List<HourlyWeatherEntity>,
        data: Weather,
        weatherDao: WeatherDao
    ) {
        val mergedHourly = mergeHourlyWeather(
            existing = existingHourly,
            incoming = data.hourly.toHourlyWeatherEntity(data.location)
        )
        weatherDao.insertWeather(
            data.current.toCurrentWeatherEntity(data.location.id),
            mergedHourly,
            data.daily.toDailyWeatherEntity(data.location.id),
            data.location.id
        )
    }

    suspend fun useGenericSaveImplementationForAlerts(
        data: AlertsDataPack,
        alertsDao: AlertsDao,
        dao: WeatherContextDao
    ) {
        alertsDao.insertAlerts(
            data.alerts.map { it.toEntity(data.location.id) } ?: emptyList(),
            data.location.id
        )
        dao.updateAlertsLastFetchedAt(data.location.id, System.currentTimeMillis())
    }

    suspend fun useGenericSaveImplementationForAirQuality(
        airQualityDao: AirQualityDao,
        data: AirQualityDataPack
    ) {
        airQualityDao.insertAirQuality(
            data.airQuality!!.current.toEntity(data.location.id),
            data.airQuality.hourly.map { it.toEntity(data.location.id) },
            data.location.id
        )
    }
}