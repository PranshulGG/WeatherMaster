package com.pranshulgg.weather_master_app.core.network.sources.weather.metoffice

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResultType
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.network.sources.weather.metoffice.model.MetOfficeForecastJson
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isWeatherCacheSafe
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnWeatherCache
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.mergeHourlyWeather
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.ApiKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.metoffice.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.repository.data.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class MetOfficeRepository @Inject constructor(
    val dao: LocationsDao,
    val weatherDao: WeatherDao,
    val api: MetOfficeApi,
    val apiKeysDao: ApiKeysDao
) : WeatherRepository {

    override val weatherSource = Source.MET_OFFICE


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


            val apiKey = apiKeysDao.getApiKeyForSource(location.source)

            when (shouldReturnCache) {
                WeatherResultType.REFRESH_TOO_EARLY -> return@withContext WeatherResult.RefreshNotAvailable
                WeatherResultType.SUCCESS -> return@withContext WeatherResult.Success(cache!!.toDomain()!!)
                else -> {}
            }


            if (apiKey?.apiKey.isNullOrBlank()) {
                return@withContext WeatherResult.Error(
                    exception = AppException.NoApiKeyError(),
                    cache?.toDomain()
                )
            }


            return@withContext try {

                val hourly = safeApiCall {
                    api.fetchHourlyForecast(
                        apiKey.apiKey, location.latitude, location.longitude
                    )
                }.getOrElse {
                    return@withContext WeatherResult.Error(
                        exception = it.toAppException(),
                        cacheWeather = cache?.toDomain()
                    )
                }

                val daily = safeApiCall {
                    api.fetchDailyForecast(
                        apiKey.apiKey, location.latitude, location.longitude
                    )
                }.getOrElse {
                    return@withContext WeatherResult.Error(
                        exception = it.toAppException(),
                        cacheWeather = cache?.toDomain()
                    )
                }


                val final = MetOfficeForecastJson(
                    hourly = hourly,
                    daily = daily
                )

                val domain = final.toDomain(location)


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

                WeatherResult.Error(
                    exception = e,
                    cache?.toDomain()
                )

            }

        }


}