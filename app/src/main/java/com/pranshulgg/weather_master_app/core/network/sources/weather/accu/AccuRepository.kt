package com.pranshulgg.weather_master_app.core.network.sources.weather.accu

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResultType
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.json.bundle.AccuWeatherBundle
import com.pranshulgg.weather_master_app.core.network.sources.weather.bmkg.BmkgApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.bmkg.json.bundle.BmkgForecastBundle
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isWeatherCacheSafe
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnWeatherCache
import com.pranshulgg.weather_master_app.data.local.dao.airquality.accu.AccuDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.entity.airquality.accu.AccuEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.accu.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.bmkg.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import javax.inject.Inject


class AccuRepository @Inject constructor(
    val dao: LocationsDao,
    val weatherDao: WeatherDao,
    val api: AccuApi,
    val accuDao: AccuDao,
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

                val locationKey = accuDao.getCityKeyForLocation(location.id)?.cityKey
                    ?: api.getLocationKey("${location.latitude},${location.longitude}").body()?.key
                    ?: return@withContext WeatherResult.Error(exception = AppException.Unknown())


                val current = api.fetchCurrent(locationKey)

                val bodyCurrent = current.body()
                    ?: return@withContext WeatherResult.Error(exception = AppException.Unknown())

                val hourly = api.fetchHourly(locationKey)

                val bodyHourly = hourly.body()
                    ?: return@withContext WeatherResult.Error(exception = AppException.Unknown())

                val daily = api.fetchDaily(locationKey)

                val bodyDaily = daily.body()
                    ?: return@withContext WeatherResult.Error(exception = AppException.Unknown())

                val final = AccuWeatherBundle(
                    current = bodyCurrent[0],
                    hourly = bodyHourly,
                    daily = bodyDaily
                )

                val domain = final.toDomain(location)

                accuDao.insertCityKey(
                    AccuEntity(
                        locationId = location.id,
                        cityKey = locationKey
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