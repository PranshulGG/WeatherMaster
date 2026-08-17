package com.pranshulgg.weather_master_app.core.network.sources.weather.imd

import android.util.Log
import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResultType
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
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
import kotlin.math.roundToInt


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

            val existingHourly = weatherDao.getHourlyDataForLocation(location.id, location.source)

            when (shouldReturnCache) {
                WeatherResultType.REFRESH_TOO_EARLY -> return@withContext WeatherResult.RefreshNotAvailable
                WeatherResultType.SUCCESS -> return@withContext WeatherResult.Success(cache!!.toDomain())
                else -> {}
            }

            return@withContext try {

                val imdTimeFrames = listOf("1hr", "3hr", "6hr")

                val timeStamps = imdTimeFrames.map {
                    safeApiCall {
                        api.fetchTimestamps("mmem_${it}.txt")
                    }.getOrElse { throwable -> return@withContext WeatherResult.Error(exception = throwable.toAppException()) }
                }

                val timeStampsBody = timeStamps.map {
                    it.string().substringBefore(",")
                }


                val latitude = roundToEighth(location.latitude)
                val longitude = roundToEighth(location.longitude)

                val forecasts = timeStampsBody.mapIndexed { index, s ->
                    safeApiCall {
                        api.fetchForecast(
                            latitude = latitude,
                            longitude = longitude,
                            date = "${s}_${imdTimeFrames[index]}_0p125"
                        )
                    }.getOrElse { return@withContext WeatherResult.Error(exception = it.toAppException()) }
                }

                val final = ImdForecastModel(
                    forecast1hr = forecasts[0],
                    forecast3hr = forecasts[1],
                    forecast6hr = forecasts[2],
                    timeStamp1 = timeStampsBody[0],
                    timeStamp2 = timeStampsBody[1],
                    timeStamp3 = timeStampsBody[2]
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

                val isCacheSafe = isWeatherCacheSafe(cache)

                WeatherResult.Error(
                    exception = e,
                    if (isCacheSafe) cache?.toDomain() else null
                )
            }
        }
}

private fun roundToEighth(value: Double): Double =
    (value * 8).roundToInt() / 8.0