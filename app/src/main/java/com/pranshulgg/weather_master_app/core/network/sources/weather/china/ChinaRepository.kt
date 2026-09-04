package com.pranshulgg.weather_master_app.core.network.sources.weather.china

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResultType
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnWeatherCache
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.mergeHourlyWeather
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.china.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.repository.weather.BaseWeatherRepository
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import com.pranshulgg.weather_master_app.data.repository.weather.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import javax.inject.Inject

class ChinaRepository @Inject constructor(
    val dao: WeatherContextDao,
    val weatherDao: WeatherDao,
    val api: ChinaApi
) : BaseWeatherRepository() {

    override val weatherSource = Source.CHINA

    override suspend fun fetchAndProcessWeather(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean,
        cacheModel: CacheModel
    ): Weather {
        val appKey = "weather20151024"
        val sign = "zUFJoAR2ZVrDy1vF3D07"

        val response = safeApiCall {
            api.getLocationKey(location.latitude, location.longitude)
        }.getOrThrow()

        val locationKey = response[0].locationKey ?: response[0].key
        ?: throw AppException.EmptyResponseBody()

        val forecastResponse = safeApiCall {
            api.getForecast(
                location.latitude,
                location.longitude,
                appKey = appKey,
                sign = sign,
                locationKey = locationKey
            )
        }.getOrThrow()
        val domain = forecastResponse.toDomain(location)

        return domain
    }

    override suspend fun saveWeatherToDb(data: Weather, cacheModel: CacheModel) {
        useGenericSaveImplementation(cacheModel.cachedHourly, data, weatherDao)
    }

    override fun finishedWeatherResult(data: Weather): WeatherResult {
        return WeatherResult.Success(weather = data)
    }
}