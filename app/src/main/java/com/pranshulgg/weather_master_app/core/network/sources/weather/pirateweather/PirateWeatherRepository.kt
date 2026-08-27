package com.pranshulgg.weather_master_app.core.network.sources.weather.pirateweather

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResultType
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResult
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResultType
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isWeatherCacheSafe
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnAlertsCache
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnWeatherCache
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.mergeHourlyWeather
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.ApiKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.sources.weatherapi.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.pirateweather.alerts.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.toEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.pirateweather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.repository.data.AlertRepository
import com.pranshulgg.weather_master_app.data.repository.data.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import javax.inject.Inject

/**
 * Initial Pirate Weather integration implemented by https://github.com/altendorfme
 */

class PirateWeatherRepository @Inject constructor(
    val dao: LocationsDao,
    val weatherDao: WeatherDao,
    val api: PirateWeatherApi,
    val apiKeysDao: ApiKeysDao,
    val alertsDao: AlertsDao,
) : WeatherRepository, AlertRepository {

    override val weatherSource = Source.PIRATE_WEATHER
    override val alertSource = Source.PIRATE_WEATHER

    override suspend fun getWeather(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean
    ): WeatherResult =
        withContext(Dispatchers.IO) {

            val cache = dao.getWeatherDataForLocation(location.id)

            val shouldReturnCache = shouldReturnWeatherCache(cache, isManualRefresh, isForceRefresh)
            val existingHourly = weatherDao.getHourlyDataForLocation(location.id, location.source)

            val apiKey = apiKeysDao.getApiKeyForSource(location.source)

            when (shouldReturnCache) {
                WeatherResultType.REFRESH_TOO_EARLY -> return@withContext WeatherResult.RefreshNotAvailable
                WeatherResultType.SUCCESS -> return@withContext WeatherResult.Success(cache?.toDomain()!!)
                else -> {}
            }


            if (apiKey?.apiKey.isNullOrBlank()) {
                return@withContext WeatherResult.Error(
                    exception = AppException.NoApiKeyError(),
                    cache?.toDomain()
                )
            }

            return@withContext try {

                val response = safeApiCall {
                    api.fetchWeather(
                        apiKey.apiKey,
                        "${location.latitude},${location.longitude}"
                    )
                }.getOrElse {
                    return@withContext WeatherResult.Error(
                        exception = it.toAppException(),
                        cache?.toDomain()
                    )
                }

                val domain = response.toDomain(location)
                val alertsDomain = response.alerts?.toDomain(location.id)

                val mergedHourly = mergeHourlyWeather(
                    existing = existingHourly,
                    incoming = domain.hourly.toHourlyWeatherEntity(location)
                )
                weatherDao.insertWeather(
                    domain.current.toCurrentWeatherEntity(location.id),
                    mergedHourly,
                    domain.daily.toDailyWeatherEntity(location.id),
                    location.id
                )
                alertsDao.insertAlerts(
                    alertsDomain?.map { it.toEntity(location.id) } ?: emptyList(),
                    location.id
                )
                dao.updateAlertsLastFetchedAt(location.id, System.currentTimeMillis())
                WeatherResult.Success(domain)

            } catch (e: UnknownHostException) {
                WeatherResult.Error(
                    exception = e.toAppException(),
                    cache?.toDomain()
                )
            } catch (e: Exception) {
                WeatherResult.Error(
                    exception = e,
                    cache?.toDomain()
                )
            }
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
