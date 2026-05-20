package com.pranshulgg.weathermaster.core.network.sources.weather.nws

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import com.pranshulgg.weathermaster.core.model.domain.location.Location
import com.pranshulgg.weathermaster.core.model.weather.WeatherResult
import com.pranshulgg.weathermaster.core.model.weather.WeatherResultType
import com.pranshulgg.weathermaster.core.network.sources.weather.nws.json.NwsCurrentForecastJson
import com.pranshulgg.weathermaster.core.network.sources.weather.nws.json.NwsStationsJson
import com.pranshulgg.weathermaster.core.network.sources.weather.nws.json.NwsStationsListJson
import com.pranshulgg.weathermaster.core.network.sources.weather.nws.json.bundle.NwsWeatherJsonBundle
import com.pranshulgg.weathermaster.core.utils.weather.cache.isWeatherCacheSafe
import com.pranshulgg.weathermaster.core.utils.weather.cache.shouldReturnWeatherCache
import com.pranshulgg.weathermaster.data.local.dao.location.LocationsDao
import com.pranshulgg.weathermaster.data.local.dao.weather.WeatherDao
import com.pranshulgg.weathermaster.data.local.dao.weather.nws.NwsDao
import com.pranshulgg.weathermaster.data.local.mapper.weather.sources.nws.toDomain
import com.pranshulgg.weathermaster.data.local.mapper.weather.sources.nws.toEntity
import com.pranshulgg.weathermaster.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weathermaster.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weathermaster.data.local.mapper.weather.toDomain
import com.pranshulgg.weathermaster.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weathermaster.data.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import javax.inject.Inject


class NwsRepository @Inject constructor(
    val dao: LocationsDao,
    val weatherDao: WeatherDao,
    val nwsDao: NwsDao,
    val api: NwsApi
) : WeatherRepository {
    override suspend fun getWeather(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean
    ): WeatherResult =
        withContext(Dispatchers.IO) {

            val cache = dao.getWeatherDataForLocation(location.id)
            val cachedGridPointsData = nwsDao.getGridPointsForLocation(location.id)

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

                    val gridPointsResponse = api.fetchGridPoints(
                        location.latitude,
                        location.longitude
                    )
                    val gridPointsBody = gridPointsResponse.body()
                        ?: return@withContext WeatherResult.Error(exception = UnknownHostException())


                    val gridPointsDomain =
                        gridPointsBody.toDomain(location, stationIdentifier = null)

                    val nwsStationsResponse = api.fetchStations(
                        gridPointsDomain.officeId,
                        gridPointsDomain.gridX,
                        gridPointsDomain.gridY
                    )

                    val nwsStationsBody = nwsStationsResponse.body()
                        ?: return@withContext WeatherResult.Error(exception = UnknownHostException())


                    // Get all the stations
                    val stations = nwsStationsBody.features.sortedBy {
                        it.properties.distance?.value
                            ?: Double.MAX_VALUE // Null distance should never become the closest (from exp)
                    }

                    val station = getValidObservationAndStation(stations, api)


                    // New domain with stationIdentifier
                    val domain = gridPointsDomain.copy(
                        stationIdentifier = station?.first
                    )

                    if (domain.stationIdentifier == null) {
                        return@withContext WeatherResult.Error(
                            exception = UnknownHostException()
                        )
                    }

                    currentObservation.value = station?.second

                    domain

                }


                // GET DAILY
                val nwsForecastResponse = api.fetchForecast(
                    nwsStationsDomain.officeId,
                    nwsStationsDomain.gridX,
                    nwsStationsDomain.gridY
                )

                val nwsForecastBody = nwsForecastResponse.body()
                    ?: return@withContext WeatherResult.Error(exception = UnknownHostException())


                // GET CURRENT
                val nwsCurrentForecastBody = if (currentObservation.value != null) {
                    currentObservation.value
                } else {
                    api.fetchCurrentForecast(nwsStationsDomain.stationIdentifier!!).body()
                } ?: return@withContext WeatherResult.Error(exception = UnknownHostException())


                // GET HOURLY
                val nwsHourlyForecastResponse =
                    api.fetchHourlyForecast(
                        nwsStationsDomain.officeId,
                        nwsStationsDomain.gridX,
                        nwsStationsDomain.gridY
                    )


                // USING FOR QuantitativePrecipitation and Snowfall
                val nwsGridPointDataResponse =
                    api.fetchGridPointData(
                        nwsStationsDomain.officeId,
                        nwsStationsDomain.gridX,
                        nwsStationsDomain.gridY
                    )


                val nwsHourlyForecastBody = nwsHourlyForecastResponse.body()
                    ?: return@withContext WeatherResult.Error(exception = UnknownHostException())

                val nwsGridPointDataBody = nwsGridPointDataResponse.body()
                    ?: return@withContext WeatherResult.Error(exception = UnknownHostException())


                // PUT EVERYTHING TOGETHER IN A BUNDLE
                val final = NwsWeatherJsonBundle(
                    current = nwsCurrentForecastBody,
                    forecast = nwsForecastBody,
                    hourly = nwsHourlyForecastBody,
                    gridPointsData = nwsGridPointDataBody
                )


                val domain = final.toDomain(location)

                nwsDao.insertLocationGridPoints(nwsStationsDomain.toEntity(location))

                weatherDao.insertWeather(
                    domain.current.toCurrentWeatherEntity(location.id),
                    domain.hourly.toHourlyWeatherEntity(location.id),
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
}


/**
 * Find a working station
 * Sometimes NWS returns empty pages or "Not Found" error
 */
private suspend fun getValidObservationAndStation(
    stations: List<NwsStationsListJson>,
    api: NwsApi
): Pair<String, NwsCurrentForecastJson?>? {

    val selectedStation = mutableStateOf<Pair<String, NwsCurrentForecastJson?>?>(null)

    for (feature in stations) {
        val stationId = feature.properties.stationIdentifier
        try {
            val response = api.fetchCurrentForecast(stationId)

            if (response.isSuccessful && response.body() != null) {
                selectedStation.value = Pair(stationId, response.body())
            }
        } catch (_: Exception) {
        }
    }

    return selectedStation.value
}