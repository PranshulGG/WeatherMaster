package com.pranshulgg.weather_master_app.core.network.sources.weather.mgm

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResultType
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.json.bundle.MgmBundle
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isWeatherCacheSafe
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnWeatherCache
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.mergeHourlyWeather
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.entity.location.LocationKeyEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.mgm.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.repository.data.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

// The daily-forecast station also serves as the current-conditions station (confirmed live:
// "merkezId" from the location lookup is what /web/sondurumlar expects, not "sondurumIstNo" -
// the latter is just informational). Hourly station is separate and can be absent for some
// rural locations, matching breezy-weather's MGM source.
private data class MgmStations(val dailyStationId: Long, val hourlyStationId: Long?)

/**
 * Initial MGM (Turkey) integration implemented by https://github.com/reveler-hub
 */
class MgmRepository @Inject constructor(
    val dao: LocationsDao,
    val weatherDao: WeatherDao,
    val api: MgmApi,
    val locationKeysDao: LocationKeysDao
) : WeatherRepository {

    override val weatherSource = Source.MGM

    override suspend fun getWeather(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean
    ): WeatherResult =
        withContext(Dispatchers.IO) {
            val cache = dao.getWeatherDataForLocation(location.id)

            val shouldReturnCache = shouldReturnWeatherCache(cache, isManualRefresh, isForceRefresh)
            val existingHourly = weatherDao.getHourlyDataForLocation(location.id, location.source)

            when (shouldReturnCache) {
                WeatherResultType.REFRESH_TOO_EARLY -> return@withContext WeatherResult.RefreshNotAvailable
                WeatherResultType.SUCCESS -> return@withContext WeatherResult.Success(cache!!.toDomain())
                else -> {}
            }

            return@withContext try {

                val stations = locationKeysDao.getCityKeyForLocation(location.id)
                    ?.cityKey
                    ?.let { parseStations(it) }
                    ?: resolveStations(location)
                    ?: return@withContext WeatherResult.Error(
                        exception = AppException.EmptyResponseBody()
                    )

                val current = safeApiCall {
                    api.fetchCurrent(stations.dailyStationId)
                }.getOrElse { return@withContext WeatherResult.Error(exception = it.toAppException()) }

                val daily = safeApiCall {
                    api.fetchDaily(stations.dailyStationId)
                }.getOrElse { return@withContext WeatherResult.Error(exception = it.toAppException()) }

                val hourly = stations.hourlyStationId?.let { hourlyStationId ->
                    safeApiCall { api.fetchHourly(hourlyStationId) }.getOrNull()
                }

                val domain = MgmBundle(
                    current = current.firstOrNull(),
                    daily = daily.firstOrNull(),
                    hourly = hourly?.firstOrNull()?.forecast,
                ).toDomain(location)

                locationKeysDao.insertCityKey(
                    LocationKeyEntity(
                        locationId = location.id,
                        cityKey = "${stations.dailyStationId}:${stations.hourlyStationId ?: ""}"
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

    private suspend fun resolveStations(location: Location): MgmStations? {
        val response = safeApiCall {
            api.fetchLocation(location.latitude, location.longitude)
        }.getOrNull() ?: return null

        val dailyStationId = response.dailyStationId ?: response.currentStationId ?: return null

        return MgmStations(dailyStationId, response.hourlyStationId)
    }

    private fun parseStations(cityKey: String): MgmStations? {
        val parts = cityKey.split(":")
        val dailyStationId = parts.getOrNull(0)?.toLongOrNull() ?: return null
        val hourlyStationId = parts.getOrNull(1)?.toLongOrNull()

        return MgmStations(dailyStationId, hourlyStationId)
    }
}
