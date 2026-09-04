package com.pranshulgg.weather_master_app.core.network.sources.weather.openweather

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResultType
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityResult
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityResultType
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json.bundle.OpenWeatherJsonBundle
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isCurrentAirQualitySafe
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnAirQualityCache
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnWeatherCache
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.mergeHourlyWeather
import com.pranshulgg.weather_master_app.data.local.dao.airquality.AirQualityDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.ApiKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.mapper.airquality.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.airquality.toEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.openweather.airquality.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.openweather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.repository.data.AirQualityRepository
import com.pranshulgg.weather_master_app.data.repository.weather.BaseWeatherRepository
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import com.pranshulgg.weather_master_app.data.repository.weather.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class OpenWeatherRepository @Inject constructor(
    val dao: WeatherContextDao,
    val weatherDao: WeatherDao,
    val api: OpenWeatherApi,
    val airQualityDao: AirQualityDao,
    val apiKeysDao: ApiKeysDao
) : BaseWeatherRepository(), AirQualityRepository {

    override val weatherSource = Source.OPEN_WEATHER
    override val airQualitySource = Source.OPEN_WEATHER

    override suspend fun fetchAndProcessWeather(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean,
        cacheModel: CacheModel
    ): Weather {
        val current = safeApiCall {
            api.fetchCurrent(
                location.latitude, location.longitude, cacheModel.apiKey!!
            )
        }.getOrThrow()

        val forecast = safeApiCall {
            api.fetchForecast(
                location.latitude, location.longitude, cacheModel.apiKey!!
            )
        }.getOrThrow()

        val final = OpenWeatherJsonBundle(
            current = current,
            forecast = forecast
        )

        val domain = final.toDomain(location)

        return domain
    }

    override suspend fun saveWeatherToDb(data: Weather, cacheModel: CacheModel) {
        val mergedHourly = mergeHourlyWeather(
            existing = cacheModel.cachedHourly,
            incoming = data.hourly.toHourlyWeatherEntity(data.location)
        )
        weatherDao.insertWeather(
            data.current.toCurrentWeatherEntity(data.location.id),
            mergedHourly,
            data.daily.toDailyWeatherEntity(data.location.id),
            data.location.id
        )
    }

    override fun finishedWeatherResult(data: Weather): WeatherResult {
        return WeatherResult.Success(data)
    }


    override suspend fun getAirQuality(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean
    ): AirQualityResult = withContext(Dispatchers.IO) {


        val cache = airQualityDao.getAirQualityForLocation(location.id)
        val shouldReturnCache = shouldReturnAirQualityCache(cache, isManualRefresh, isForceRefresh)
        val apiKey = apiKeysDao.getApiKeyForSource(location.source)


        when (shouldReturnCache) {
            AirQualityResultType.RETURN_CACHE -> return@withContext AirQualityResult.Success(cache!!.toDomain()!!)
            else -> {}
        }

        val isCacheSafe = isCurrentAirQualitySafe(cache?.toDomain())

        if (apiKey?.apiKey.isNullOrBlank()) {
            return@withContext AirQualityResult.Error(
                exception = AppException.NoApiKeyError(),
                cacheAirQuality = if (isCacheSafe) cache?.toDomain() else null
            )
        }


        return@withContext try {
            val airQuality = safeApiCall {
                api.fetchAirQuality(location.latitude, location.longitude, apiKey.apiKey)
            }.getOrElse {
                return@withContext AirQualityResult.Error(
                    exception = it.toAppException(),
                    cacheAirQuality = cache?.toDomain()
                )
            }


            val domain = airQuality.toDomain(location)

            airQualityDao.insertAirQuality(
                domain.current.toEntity(location.id),
                domain.hourly.map { it.toEntity(location.id) },
                location.id
            )

            AirQualityResult.Success(domain)
        } catch (e: Exception) {


            AirQualityResult.Error(exception = e, cache?.toDomain())
        }
    }
}