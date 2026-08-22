package com.pranshulgg.weather_master_app.core.network.sources.weather.nws

import androidx.compose.runtime.mutableStateOf
import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResultType
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResult
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResultType
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.json.NwsCurrentForecastJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.json.NwsStationsListJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.nws.json.bundle.NwsWeatherJsonBundle
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isWeatherCacheSafe
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnAlertsCache
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnWeatherCache
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.mergeHourlyWeather
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.nws.NwsDao
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.toEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.nws.alerts.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.nws.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.nws.toEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.repository.data.AlertRepository
import com.pranshulgg.weather_master_app.data.repository.data.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class NwsRepository @Inject constructor(
    val dao: LocationsDao,
    val weatherDao: WeatherDao,
    val nwsDao: NwsDao,
    val api: NwsApi,
    val alertsDao: AlertsDao
) : WeatherRepository, AlertRepository {

    override val weatherSource = Source.NWS
    override val alertSource = Source.NWS


    override suspend fun getWeather(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean
    ): WeatherResult =
        withContext(Dispatchers.IO) {

            val cache = dao.getWeatherDataForLocation(location.id)
            val cachedGridPointsData = nwsDao.getGridPointsForLocation(location.id)
            val existingHourly = weatherDao.getHourlyDataForLocation(location.id, location.source)


            val shouldReturnCache = shouldReturnWeatherCache(cache, isManualRefresh, isForceRefresh)

            when (shouldReturnCache) {
                WeatherResultType.REFRESH_TOO_EARLY -> return@withContext WeatherResult.RefreshNotAvailable
                WeatherResultType.SUCCESS -> return@withContext WeatherResult.Success(cache!!.toDomain())
                else -> {}
            }

            /**
             * NWS has everything as a separate endpoints
             * Makes it annoying to get the data, but we'll still do it cuz why not
             * Sequential flow, cache the annoying data (e.g. grid points and station, but we'll still update it time to time)
             */
            return@withContext try {

                val currentObservation = mutableStateOf<NwsCurrentForecastJson?>(null)

                val nwsStationsDomain = if (cachedGridPointsData != null) {
                    cachedGridPointsData.toDomain()
                } else {

                    val gridPoint = safeApiCall {
                        api.fetchGridPoints(
                            location.latitude,
                            location.longitude
                        )
                    }.getOrElse { return@withContext WeatherResult.Error(exception = it.toAppException()) }


                    val gridPointsDomain = gridPoint.toDomain(location, stationIdentifier = null)

                    val nwsStations = safeApiCall {
                        api.fetchStations(
                            gridPointsDomain.officeId,
                            gridPointsDomain.gridX,
                            gridPointsDomain.gridY
                        )
                    }.getOrElse { return@withContext WeatherResult.Error(exception = it.toAppException()) }


                    // Get all the stations
                    val stations = nwsStations.features

                    val station = getValidObservationAndStation(stations, api)


                    // New domain with stationIdentifier
                    val domain = gridPointsDomain.copy(
                        stationIdentifier = station?.first
                    )

                    if (domain.stationIdentifier == null) {
                        return@withContext WeatherResult.Error(
                            exception = AppException.Unknown(),
                        )
                    }

                    currentObservation.value = station?.second

                    domain

                }


                // GET DAILY
                val nwsForecast = safeApiCall {
                    api.fetchForecast(
                        nwsStationsDomain.officeId,
                        nwsStationsDomain.gridX,
                        nwsStationsDomain.gridY
                    )
                }.getOrElse { return@withContext WeatherResult.Error(exception = it.toAppException()) }

                // GET CURRENT
                val nwsCurrentForecastBody = if (currentObservation.value != null) {
                    currentObservation.value
                } else {
                    safeApiCall { api.fetchCurrentForecast(nwsStationsDomain.stationIdentifier!!) }.getOrElse {
                        return@withContext WeatherResult.Error(
                            exception = it.toAppException()
                        )
                    }
                } ?: return@withContext WeatherResult.Error(AppException.EmptyResponseBody())

                // GET HOURLY
                val nwsHourlyForecast =
                    safeApiCall {
                        api.fetchHourlyForecast(
                            nwsStationsDomain.officeId,
                            nwsStationsDomain.gridX,
                            nwsStationsDomain.gridY
                        )
                    }.getOrElse { return@withContext WeatherResult.Error(exception = it.toAppException()) }

                // USING FOR QuantitativePrecipitation and Snowfall
                val nwsGridPointData = safeApiCall {
                    api.fetchGridPointData(
                        nwsStationsDomain.officeId,
                        nwsStationsDomain.gridX,
                        nwsStationsDomain.gridY
                    )
                }.getOrElse { return@withContext WeatherResult.Error(exception = it.toAppException()) }

                // PUT EVERYTHING TOGETHER IN A BUNDLE
                val final = NwsWeatherJsonBundle(
                    current = nwsCurrentForecastBody,
                    forecast = nwsForecast,
                    hourly = nwsHourlyForecast,
                    gridPointsData = nwsGridPointData
                )


                val domain = final.toDomain(location)

                val mergedHourly = mergeHourlyWeather(
                    existing = existingHourly,
                    incoming = domain.hourly.toHourlyWeatherEntity(location)
                )


                nwsDao.insertLocationGridPoints(nwsStationsDomain.toEntity(location))

                weatherDao.insertWeather(
                    domain.current.toCurrentWeatherEntity(location.id),
                    mergedHourly,
                    domain.daily.toDailyWeatherEntity(location.id),
                    location.id

                )

                return@withContext WeatherResult.Success(domain)

            } catch (e: Exception) {

                val isCacheSafe = isWeatherCacheSafe(cache)

                WeatherResult.Error(
                    exception = e,
                    if (isCacheSafe) cache?.toDomain() else null
                )

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

        if (shouldReturnCache == AlertResultType.RETURN_CACHE) {
            return@withContext AlertResult.Success(cache.map { it!!.toDomain() })
        }

        val point = "${location.latitude},${location.longitude}"

        safeApiCall { api.fetchActiveAlerts(point) }.fold(
            onSuccess = { body ->
                val domain = body.toDomain(location.id)

                alertsDao.insertAlerts(domain.map { it.toEntity(location.id) }, location.id)
                dao.updateAlertsLastFetchedAt(location.id, System.currentTimeMillis())

                AlertResult.Success(domain)
            },
            onFailure = { e ->
                AlertResult.Error(
                    exception = e as? Exception ?: Exception(e),
                    cacheAlerts = cache.map { it!!.toDomain() }
                )
            }
        )
    }
}


/**
 * Find a working station
 * Sometimes NWS returns empty pages or "Not Found" error
 */
private suspend fun getValidObservationAndStation(
    stations: List<NwsStationsListJson>,
    api: NwsApi
): Pair<String, NwsCurrentForecastJson?>? {


    for (feature in stations) {
        val stationId = feature.properties.stationIdentifier

        try {
            val response = api.fetchCurrentForecast(stationId)

            if (response.isSuccessful && response.body() != null) {
                return Pair(stationId, response.body())
            }
        } catch (_: Exception) {
        }
    }

    return null
}