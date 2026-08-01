package com.pranshulgg.weather_master_app.core.network.sources.weather.ipma

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResultType
import com.pranshulgg.weather_master_app.core.network.sources.weather.ipma.json.IpmaLocationsJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.meteoam.MeteoamApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.meteoam.json.bundle.MeteoamWeatherBundle
import com.pranshulgg.weather_master_app.core.utils.formatters.toSafeDouble
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isWeatherCacheSafe
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnWeatherCache
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.entity.location.LocationKeyEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.ipma.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.meteoam.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class IpmaRepository @Inject constructor(
    val dao: LocationsDao,
    val weatherDao: WeatherDao,
    val api: IpmaApi,
    val locationKeysDao: LocationKeysDao
) : WeatherRepository {


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


            when (shouldReturnCache) {
                WeatherResultType.REFRESH_TOO_EARLY -> return@withContext WeatherResult.RefreshNotAvailable
                WeatherResultType.SUCCESS -> return@withContext WeatherResult.Success(cache!!.toDomain())
                else -> {}
            }

            return@withContext try {

                val locationId =
                    locationKeysDao.getCityKeyForLocation(location.id)?.cityKey.toSafeDouble()
                        ?.toLong()
                        ?: getClosestLocation(api.fetchLocations().body(), location)
                        ?: return@withContext WeatherResult.Error(
                            exception = AppException.Unknown()
                        )

                val forecast = api.fetchForecast(locationId)

                val bodyForecast = forecast.body()
                    ?: return@withContext WeatherResult.Error(exception = AppException.Unknown())

                val domain = bodyForecast.toDomain(location)

                locationKeysDao.insertCityKey(
                    LocationKeyEntity(
                        locationId = location.id,
                        cityKey = locationId.toString()
                    )
                )
                weatherDao.insertWeather(
                    domain.current.toCurrentWeatherEntity(location.id),
                    domain.hourly.toHourlyWeatherEntity(location.id),
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

}

private fun getClosestLocation(locations: List<IpmaLocationsJson>?, location: Location): Long? {
    var closestDistance = Float.MAX_VALUE

    if (locations == null) return null

    var closestId: Long? = null
    for (i in locations) {


        val lat = i.latitude.toSafeDouble()
        val lon = i.longitude.toSafeDouble()
        val id = i.globalIdLocal

        if (lat != null && lon != null) {
            val results = FloatArray(1)

            android.location.Location.distanceBetween(
                location.latitude,
                location.longitude,
                lat,
                lon,
                results
            )

            if (results[0] < closestDistance) {
                closestDistance = results[0]
                closestId = id
            }
        }
    }

    return closestId
}