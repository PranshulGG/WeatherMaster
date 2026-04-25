package com.pranshulgg.weathermaster.core.network.openmeteo

import android.util.Log
import com.pranshulgg.weathermaster.core.model.WeatherResult
import com.pranshulgg.weathermaster.core.model.WeatherResultType
import com.pranshulgg.weathermaster.core.model.domain.Location
import com.pranshulgg.weathermaster.core.utils.WeatherUtils
import com.pranshulgg.weathermaster.data.local.dao.WeatherDataDao
import com.pranshulgg.weathermaster.data.local.mapper.toDomain
import com.pranshulgg.weathermaster.data.local.mapper.weatherProviders.toCurrentWeatherEntity
import com.pranshulgg.weathermaster.data.local.mapper.weatherProviders.toDailyWeatherEntity
import com.pranshulgg.weathermaster.data.local.mapper.weatherProviders.toDomain
import com.pranshulgg.weathermaster.data.local.mapper.weatherProviders.toHourlyWeatherEntity
import com.pranshulgg.weathermaster.data.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class OpenMeteoRepository @Inject constructor(
    val dao: WeatherDataDao,
    val api: OpenMeteoApi
) : WeatherRepository {

    override suspend fun getWeather(location: Location, isRefresh: Boolean): WeatherResult =
        withContext(
            Dispatchers.IO
        ) {

            val cache = dao.getAllWeatherDataForLocation(location.id)

            val shouldReturnCache = WeatherUtils.shouldReturnCache(cache, isRefresh)


            when (shouldReturnCache) {
                WeatherResultType.REFRESH_TOO_EARLY -> return@withContext WeatherResult.RefreshNotAvailable
                WeatherResultType.SUCCESS -> return@withContext WeatherResult.Success(cache!!.toDomain())
                else -> {}
            }

            return@withContext try {

                val response =
                    api.fetchWeather(location.latitude, location.longitude, location.timezone)

                val body =
                    response.body() ?: return@withContext WeatherResult.Error("Empty response")

                val domain = body.toDomain(location)


                dao.insertWeather(
                    domain.current.toCurrentWeatherEntity(location.id),
                    domain.hourly.toHourlyWeatherEntity(location.id),
                    domain.daily.toDailyWeatherEntity(location.id)
                )

                WeatherResult.Success(domain)

            } catch (e: Exception) {

                val isCacheSafe = WeatherUtils.isCacheSafe(cache)

                WeatherResult.Error(
                    e.message ?: "Unknown error",
                    if (isCacheSafe) cache?.toDomain() else null
                )

            }

        }


}