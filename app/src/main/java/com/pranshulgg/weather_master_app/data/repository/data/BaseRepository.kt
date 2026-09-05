package com.pranshulgg.weather_master_app.data.repository.data

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.weather.WeatherDataPack
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.mergeHourlyWeather
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.entity.weather.HourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.repository.alerts.AlertRepository
import com.pranshulgg.weather_master_app.data.repository.capability.AlertCapability
import com.pranshulgg.weather_master_app.data.repository.capability.WeatherCapability
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModelResultType
import com.pranshulgg.weather_master_app.data.repository.weather.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseRepository : WeatherRepository, AlertRepository {

    protected abstract fun weatherCapability(): WeatherCapability?
    protected abstract fun alertCapability(): AlertCapability?

    final override suspend fun getWeather(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean,
        cacheModel: CacheModel
    ): WeatherResult =
        withContext(Dispatchers.IO) {

            val capability = weatherCapability()
                ?: error("This source does not support weather")

            val cache = cacheModel.cachedWeather

            val data = try {

                if (cacheModel.type == CacheModelResultType.NO_API_KEY_ERROR) {
                    return@withContext WeatherResult.Error(
                        exception = AppException.NoApiKeyError(),
                        weather = cache
                    )
                }

                if (cacheModel.type == CacheModelResultType.FETCH || cache == null) {
                    capability.fetchAndProcess(
                        location,
                        isManualRefresh,
                        isForceRefresh,
                        cacheModel
                    )
                } else if (cacheModel.type == CacheModelResultType.REFRESH_TOO_EARLY) {
                    return@withContext WeatherResult.RefreshNotAvailable(weather = cache)
                } else WeatherDataPack(weather = cache)

            } catch (e: Exception) {
                return@withContext WeatherResult.Error(
                    exception = e.toAppException(),
                    weather = cache
                )
            }

            try {
                capability.saveAdditionalDataToDb(pack = data)
                capability.saveToDb(data.weather, cacheModel)
            } catch (e: Exception) {
                return@withContext WeatherResult.Error(
                    exception = e.toAppException(),
                    weather = cache
                )
            }

            val finished = capability.finishedResult(data.weather)

            WeatherResult.Success(weather = finished.weather)
        }


    suspend fun useGenericSaveImplementationForWeather(
        existingHourly: List<HourlyWeatherEntity>,
        data: Weather,
        weatherDao: WeatherDao
    ) {
        val mergedHourly = mergeHourlyWeather(
            existing = existingHourly,
            incoming = data.hourly.toHourlyWeatherEntity(data.location)
        )
        weatherDao.insertWeather(
            data.current.toCurrentWeatherEntity(data.location.id),
            mergedHourly,
            data.daily.toDailyWeatherEntity(data.location.id),
            data.location.id
        )
    }

}