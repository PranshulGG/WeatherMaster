package com.pranshulgg.weather_master_app.core.network.sources.weather.imd

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResultType
import com.pranshulgg.weather_master_app.core.network.sources.weather.imd.model.ImdForecastModel
import com.pranshulgg.weather_master_app.core.network.sources.weather.meteoam.MeteoamApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.meteoam.json.bundle.MeteoamWeatherBundle
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isWeatherCacheSafe
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnWeatherCache
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.mergeHourlyWeather
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.imd.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.meteoam.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ImdRepository @Inject constructor(
    val dao: LocationsDao,
    val weatherDao: WeatherDao,
    val api: ImdApi
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

            val existingHourly = weatherDao.getHourlyDataForLocation(location.id)

            when (shouldReturnCache) {
                WeatherResultType.REFRESH_TOO_EARLY -> return@withContext WeatherResult.RefreshNotAvailable
                WeatherResultType.SUCCESS -> return@withContext WeatherResult.Success(cache!!.toDomain())
                else -> {}
            }

            return@withContext try {

                val imdTimeFrames = listOf("1hr", "3hr", "6hr")

                val timeStamps = imdTimeFrames.map {
                    api.fetchTimestamps("mmem_${it}.txt")
                }

                val timeStampsBody = timeStamps.map {
                    it.body()?.string()?.substringBefore(",")
                }

                val forecasts = timeStampsBody.mapIndexed { index, s ->
                    api.fetchForecast(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        date = "${s}_${imdTimeFrames[index]}_0p125"
                    )
                }

                val final = ImdForecastModel(
                    forecast1hr = forecasts[0].body(),
                    forecast3hr = forecasts[1].body(),
                    forecast6hr = forecasts[2].body(),
                    timeStamp1 = timeStampsBody[0]!!,
                    timeStamp2 = timeStampsBody[1]!!,
                    timeStamp3 = timeStampsBody[2]!!
                )

                val domain = final.toDomain(location)

                val mergedHourly = mergeHourlyWeather(
                    existing = existingHourly,
                    incoming = domain.hourly.toHourlyWeatherEntity(location.id)
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

}