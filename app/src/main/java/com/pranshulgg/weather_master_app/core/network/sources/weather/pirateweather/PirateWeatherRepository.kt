package com.pranshulgg.weather_master_app.core.network.sources.weather.pirateweather

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.FinishedWeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResult
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResultType
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnAlertsCache
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.mergeHourlyWeather
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.ApiKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.pirateweather.alerts.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.toEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.pirateweather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.repository.alerts.AlertRepository
import com.pranshulgg.weather_master_app.data.repository.weather.BaseWeatherRepository
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import com.pranshulgg.weather_master_app.data.repository.weather.WeatherAdditionalData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Initial Pirate Weather integration implemented by https://github.com/altendorfme
 */

class PirateWeatherRepository @Inject constructor(
    val dao: WeatherContextDao,
    val weatherDao: WeatherDao,
    val api: PirateWeatherApi,
    val apiKeysDao: ApiKeysDao,
    val alertsDao: AlertsDao,
) : BaseWeatherRepository(), AlertRepository {

    override val weatherSource = Source.PIRATE_WEATHER
    override val alertSource = Source.PIRATE_WEATHER
    override val providesAlerts = true


    override suspend fun fetchAndProcessWeather(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean,
        cacheModel: CacheModel
    ): Weather {

        val response = safeApiCall {
            api.fetchWeather(
                cacheModel.apiKey!!,
                "${location.latitude},${location.longitude}"
            )
        }.getOrThrow()

        val domain = response.toDomain(location)

        setAdditionalData(
            alerts = response.alerts?.toDomain(location.id) ?: emptyList()
        )

        return domain
    }

    override suspend fun saveAdditionalData(additionalData: WeatherAdditionalData, data: Weather) {
        alertsDao.insertAlerts(
            additionalData.alerts.map { it.toEntity(data.location.id) } ?: emptyList(),
            data.location.id
        )
        dao.updateAlertsLastFetchedAt(data.location.id, System.currentTimeMillis())
    }

    override suspend fun saveWeatherToDb(data: Weather, cacheModel: CacheModel) {
        val mergedHourly = mergeHourlyWeather(
            existing = cacheModel.cachedHourly,
            incoming = data.hourly.toHourlyWeatherEntity(data.location)
        )
        weatherDao.insertWeather(
            data.current.toCurrentWeatherEntity(data.location.id),
            mergedHourly,
            data.daily.toDailyWeatherEntity(data.location.id),
            data.location.id
        )
    }

    override fun finishedWeatherResult(data: Weather): FinishedWeatherResult {
        return FinishedWeatherResult(weather = data)
    }

    override suspend fun getAlerts(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean
    ): AlertResult = withContext(Dispatchers.IO) {

        val cache = alertsDao.getAlertsForLocation(location.id)

        if (location.source == location.alertSource) {
            AlertResult.Success(cache.map { it!!.toDomain() })
        } else {
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

            val apiKey = apiKeysDao.getApiKeyForSource(location.alertSource)

            if (apiKey?.apiKey.isNullOrBlank()) {
                return@withContext AlertResult.Success(cache.map { it!!.toDomain() })
            }

            return@withContext try {

                val response = safeApiCall {
                    api.fetchWeather(
                        apiKey.apiKey,
                        "${location.latitude},${location.longitude}"
                    )
                }.getOrElse {
                    return@withContext AlertResult.Error(
                        exception = it.toAppException(),
                        cacheAlerts = cache.map { cache -> cache!!.toDomain() }
                    )
                }

                val domain = response.alerts?.toDomain(location.id)

                alertsDao.insertAlerts(
                    domain?.map { it.toEntity(location.id) } ?: emptyList(),
                    location.id
                )
                dao.updateAlertsLastFetchedAt(location.id, System.currentTimeMillis())

                AlertResult.Success(domain ?: emptyList())

            } catch (e: Exception) {
                AlertResult.Error(exception = e, cacheAlerts = cache.map { it!!.toDomain() })
            }

        }
    }
}
