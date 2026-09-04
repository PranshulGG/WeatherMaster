package com.pranshulgg.weather_master_app.data.repository.weather

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.nws.NwsGridPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class WeatherAdditionalData(
    val alerts: List<Alert> = emptyList(),
    val airQuality: AirQuality? = null,
    val locationKey: String? = null,
    val nwsGridPoints: NwsGridPoints? = null
)

abstract class BaseWeatherRepository : WeatherRepository {
    private var additionalData = WeatherAdditionalData()
    final override suspend fun getWeather(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean,
        cacheModel: CacheModel
    ): WeatherResult =
        withContext(Dispatchers.IO) {

            val cache = cacheModel.cachedWeather

            val data = try {

                if (cacheModel.type == CacheModelResultType.NO_API_KEY_ERROR) {
                    return@withContext WeatherResult.Error(
                        exception = AppException.NoApiKeyError(),
                        weather = cache
                    )
                }

                if (cacheModel.type == CacheModelResultType.FETCH || cache == null) {
                    fetchAndProcessWeather(
                        location,
                        isManualRefresh,
                        isForceRefresh,
                        cacheModel
                    )
                } else if (cacheModel.type == CacheModelResultType.REFRESH_TOO_EARLY) {
                    return@withContext WeatherResult.RefreshNotAvailable(weather = cache)
                } else cache

            } catch (e: Exception) {
                return@withContext WeatherResult.Error(
                    exception = e.toAppException(),
                    weather = cache
                )
            }

            try {
                saveAdditionalData(additionalData, data)
                saveWeatherToDb(data, cacheModel)
            } catch (e: Exception) {
                return@withContext WeatherResult.Error(
                    exception = e.toAppException(),
                    weather = cache
                )
            }

            finishedWeatherResult(data)

        }

    protected abstract suspend fun fetchAndProcessWeather(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean,
        cacheModel: CacheModel
    ): Weather

    protected abstract suspend fun saveWeatherToDb(
        data: Weather,
        cacheModel: CacheModel
    )

    protected abstract fun finishedWeatherResult(
        data: Weather
    ): WeatherResult


    // For sources that may provide alerts/air quality in the same API call
    protected fun setAdditionalData(
        alerts: List<Alert> = emptyList(),
        airQuality: AirQuality? = null,
        locationKey: String? = null,
        nwsGridPoints: NwsGridPoints? = null
    ) {
        additionalData = WeatherAdditionalData(
            alerts = alerts,
            airQuality = airQuality,
            locationKey = locationKey,
            nwsGridPoints = nwsGridPoints
        )
    }

    protected open suspend fun saveAdditionalData(
        additionalData: WeatherAdditionalData,
        data: Weather
    ) {
        // No additional data by default
    }


}