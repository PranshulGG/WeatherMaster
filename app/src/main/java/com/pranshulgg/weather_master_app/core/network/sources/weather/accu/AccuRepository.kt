package com.pranshulgg.weather_master_app.core.network.sources.weather.accu

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResultType
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityResult
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityResultType
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResult
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResultType
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.airquality.json.bundle.AccuAqiJsonBundle
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.json.bundle.AccuWeatherBundle
import com.pranshulgg.weather_master_app.core.utils.locale.getCurrentAppLocale
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isCurrentAirQualitySafe
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isWeatherCacheSafe
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnAirQualityCache
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnAlertsCache
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnWeatherCache
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.mergeHourlyWeather
import com.pranshulgg.weather_master_app.data.local.dao.airquality.AirQualityDao
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.entity.location.LocationKeyEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.accu.airquality.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.airquality.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.airquality.toEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.accu.alerts.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.toEntity
import com.pranshulgg.weather_master_app.data.local.mapper.locations.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.accu.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.repository.data.AirQualityRepository
import com.pranshulgg.weather_master_app.data.repository.data.AlertRepository
import com.pranshulgg.weather_master_app.data.repository.data.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import javax.inject.Inject


class AccuRepository @Inject constructor(
    val dao: LocationsDao,
    val weatherDao: WeatherDao,
    val api: AccuApi,
    val locationKeysDao: LocationKeysDao,
    val airQualityDao: AirQualityDao,
    val alertsDao: AlertsDao,
    val locationsDao: LocationsDao
) : WeatherRepository, AirQualityRepository, AlertRepository {

    override val weatherSource = Source.ACCU_WEATHER
    override val airQualitySource = Source.ACCU_WEATHER
    override val alertSource = Source.ACCU_WEATHER

    override suspend fun getWeather(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean
    ): WeatherResult =
        withContext(
            Dispatchers.IO
        ) {
            val cache = dao.getWeatherDataForLocation(location.id)

            val shouldReturnCache = shouldReturnWeatherCache(cache, isManualRefresh, isForceRefresh)
            val existingHourly = weatherDao.getHourlyDataForLocation(location.id, location.source)


            when (shouldReturnCache) {
                WeatherResultType.REFRESH_TOO_EARLY -> return@withContext WeatherResult.RefreshNotAvailable
                WeatherResultType.SUCCESS -> return@withContext WeatherResult.Success(cache!!.toDomain())
                else -> {}
            }

            return@withContext try {

                val locationKey = locationKeysDao.getCityKeyForLocation(location.id)?.cityKey
                    ?: safeApiCall { api.getLocationKey("${location.latitude},${location.longitude}") }.getOrElse {
                        return@withContext WeatherResult.Error(
                            exception = it.toAppException()
                        )
                    }.key

                val current = safeApiCall {
                    api.fetchCurrent(locationKey)
                }.getOrElse { return@withContext WeatherResult.Error(exception = it.toAppException()) }


                val hourly = safeApiCall {
                    api.fetchHourly(locationKey)
                }.getOrElse { return@withContext WeatherResult.Error(exception = it.toAppException()) }


                val daily = safeApiCall { api.fetchDaily(locationKey) }.getOrElse {
                    return@withContext WeatherResult.Error(exception = it.toAppException())
                }


                val final = AccuWeatherBundle(
                    current = current[0],
                    hourly = hourly,
                    daily = daily
                )

                val domain = final.toDomain(location)

                locationKeysDao.insertCityKey(
                    LocationKeyEntity(
                        locationId = location.id,
                        cityKey = locationKey
                    )
                )

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
                WeatherResult.Success(domain)

            } catch (e: Exception) {

                val isCacheSafe = isWeatherCacheSafe(cache)

                WeatherResult.Error(
                    exception = e,
                    if (isCacheSafe) cache?.toDomain() else null
                )

            }


        }

    override suspend fun getAirQuality(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean
    ): AirQualityResult = withContext(Dispatchers.IO) {


        val cache = airQualityDao.getAirQualityForLocation(location.id)
        val shouldReturnCache = shouldReturnAirQualityCache(cache, isManualRefresh, isForceRefresh)

        when (shouldReturnCache) {
            AirQualityResultType.RETURN_CACHE -> return@withContext AirQualityResult.Success(cache!!.toDomain())
            else -> {}
        }

        return@withContext try {

            val locationKey =
                locationKeysDao.getCityKeyForLocation(location.id)?.toDomain()?.cityKey
                    ?: api.getLocationKey("${location.latitude},${location.longitude}")
                        .body()?.key
                    ?: return@withContext AirQualityResult.Error(exception = AppException.Unknown())


            val responseCurrent = api.fetchCurrentAirQuality(locationKey)

            val bodyCurrent = responseCurrent.body()
                ?: return@withContext AirQualityResult.Error(exception = UnknownHostException())

            val responseForecast = api.fetchAirQualityForecast(locationKey)

            val bodyForecast = responseForecast.body()
                ?: return@withContext AirQualityResult.Error(exception = UnknownHostException())

            val final = AccuAqiJsonBundle(
                current = bodyCurrent,
                forecast = bodyForecast
            )

            val domain = final.toDomain()

            locationKeysDao.insertCityKey(
                LocationKeyEntity(
                    locationId = location.id,
                    cityKey = locationKey
                )
            )

            airQualityDao.insertAirQuality(
                domain.current.toEntity(location.id),
                domain.hourly.map { it.toEntity(location.id) },
                location.id
            )

            AirQualityResult.Success(domain)
        } catch (e: Exception) {

            val isCacheSafe = isCurrentAirQualitySafe(cache?.toDomain())

            AirQualityResult.Error(exception = e, if (isCacheSafe) cache?.toDomain() else null)
        }
    }

    override suspend fun getAlerts(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean
    ): AlertResult = withContext(Dispatchers.IO) {

        val cache = alertsDao.getAlertsForLocation(location.id)

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
            val locationKey =
                locationKeysDao.getCityKeyForLocation(location.id)?.toDomain()?.cityKey
                    ?: api.getLocationKey("${location.latitude},${location.longitude}")
                        .body()?.key
                    ?: return@withContext AlertResult.Error(exception = AppException.Unknown())

            val response = api.fetchAlerts(locationKey, language = getCurrentAppLocale().language)
            val body = response.body()
                ?: return@withContext AlertResult.Error(exception = AppException.Unknown())

            val domain = body.map { it.toDomain(location.id) }

            locationKeysDao.insertCityKey(
                LocationKeyEntity(
                    locationId = location.id,
                    cityKey = locationKey
                )
            )
            alertsDao.insertAlerts(
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