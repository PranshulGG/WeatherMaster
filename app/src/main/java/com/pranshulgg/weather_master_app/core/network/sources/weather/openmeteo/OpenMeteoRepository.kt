package com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.FinishedWeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResultType
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityResult
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityResultType
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo.airquality.OpenMeteoAqiApi
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isCurrentAirQualitySafe
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnAirQualityCache
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnWeatherCache
import com.pranshulgg.weather_master_app.data.local.dao.airquality.AirQualityDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.openmeteo.airquality.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.airquality.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.airquality.toEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.openmeteo.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.repository.data.AirQualityRepository
import com.pranshulgg.weather_master_app.data.repository.weather.BaseWeatherRepository
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModelResultType
import com.pranshulgg.weather_master_app.data.repository.weather.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import javax.inject.Inject

class OpenMeteoRepository @Inject constructor(
    val dao: WeatherContextDao,
    val weatherDao: WeatherDao,
    val api: OpenMeteoApi,
    val airQualityApi: OpenMeteoAqiApi,
    val airQualityDao: AirQualityDao
) : BaseWeatherRepository(), AirQualityRepository {

    override val weatherSource = Source.OPEN_METEO
    override val airQualitySource = Source.OPEN_METEO

    override suspend fun fetchAndProcessWeather(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean,
        cacheModel: CacheModel
    ): Weather {

        val response = safeApiCall {
            api.fetchWeather(
                location.latitude,
                location.longitude,
                location.timezone,
                model = location.openMeteoModel.modelId
            )
        }.getOrThrow()

        val domain = response.toDomain(location)

        return domain
    }

    override suspend fun saveWeatherToDb(data: Weather, cacheModel: CacheModel) {

        weatherDao.insertWeather(
            data.current.toCurrentWeatherEntity(data.location.id),
            data.hourly.toHourlyWeatherEntity(data.location),
            data.daily.toDailyWeatherEntity(data.location.id),
            data.location.id
        )

    }

    override fun finishedWeatherResult(data: Weather): FinishedWeatherResult {
        return FinishedWeatherResult(weather = data)
    }

    override suspend fun getAirQuality(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean
    ): AirQualityResult = withContext(Dispatchers.IO) {


        val cache = airQualityDao.getAirQualityForLocation(location.id)
        val shouldReturnCache = shouldReturnAirQualityCache(cache, isManualRefresh, isForceRefresh)

        when (shouldReturnCache) {
            AirQualityResultType.RETURN_CACHE -> return@withContext AirQualityResult.Success(cache!!.toDomain()!!)
            else -> {}
        }

        return@withContext try {
            val response = airQualityApi.fetchAirQuality(location.latitude, location.longitude)

            val body = response.body()
                ?: return@withContext AirQualityResult.Error(
                    exception = UnknownHostException(),
                    cacheAirQuality = cache?.toDomain()
                )

            val domain = body.toDomain()

            airQualityDao.insertAirQuality(
                domain.current.toEntity(location.id),
                domain.hourly.map { it.toEntity(location.id) },
                location.id
            )

            AirQualityResult.Success(domain)
        } catch (e: Exception) {

            val isCacheSafe = isCurrentAirQualitySafe(cache?.toDomain())

            AirQualityResult.Error(exception = e, if (isCacheSafe) cache?.toDomain() else null)
        }
    }
}