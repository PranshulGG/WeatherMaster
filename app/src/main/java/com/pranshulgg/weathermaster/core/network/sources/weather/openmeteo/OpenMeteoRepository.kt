package com.pranshulgg.weathermaster.core.network.sources.weather.openmeteo

import com.pranshulgg.weathermaster.core.model.domain.location.Location
import com.pranshulgg.weathermaster.core.model.weather.WeatherResult
import com.pranshulgg.weathermaster.core.model.weather.WeatherResultType
import com.pranshulgg.weathermaster.core.utils.weather.cache.isWeatherCacheSafe
import com.pranshulgg.weathermaster.core.utils.weather.cache.shouldReturnWeatherCache
import com.pranshulgg.weathermaster.data.local.dao.location.LocationsDao
import com.pranshulgg.weathermaster.data.local.dao.weather.WeatherDao
import com.pranshulgg.weathermaster.data.local.mapper.weather.sources.openmeteo.toCurrentWeatherEntity
import com.pranshulgg.weathermaster.data.local.mapper.weather.sources.openmeteo.toDailyWeatherEntity
import com.pranshulgg.weathermaster.data.local.mapper.weather.sources.openmeteo.toDomain
import com.pranshulgg.weathermaster.data.local.mapper.weather.sources.openmeteo.toHourlyWeatherEntity
import com.pranshulgg.weathermaster.data.local.mapper.weather.toDomain
import com.pranshulgg.weathermaster.data.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import javax.inject.Inject

class OpenMeteoRepository @Inject constructor(
    val dao: LocationsDao,
    val weatherDao: WeatherDao,
    val api: OpenMeteoApi
) : WeatherRepository {

    override suspend fun getWeather(location: Location, isManualRefresh: Boolean): WeatherResult =
        withContext(
            Dispatchers.IO
        ) {

            val cache = dao.getWeatherDataForLocation(location.id)

            val shouldReturnCache = shouldReturnWeatherCache(cache, isManualRefresh)

            when (shouldReturnCache) {
                WeatherResultType.REFRESH_TOO_EARLY -> return@withContext WeatherResult.RefreshNotAvailable
                WeatherResultType.SUCCESS -> return@withContext WeatherResult.Success(cache!!.toDomain())
                else -> {}
            }

            return@withContext try {

                val response =
                    api.fetchWeather(location.latitude, location.longitude, location.timezone)

                val body =
                    response.body()
                        ?: return@withContext WeatherResult.Error(exception = UnknownHostException())

                val domain = body.toDomain(location)


                weatherDao.insertWeather(
                    domain.current.toCurrentWeatherEntity(location.id),
                    domain.hourly.toHourlyWeatherEntity(location.id),
                    domain.daily.toDailyWeatherEntity(location.id)
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