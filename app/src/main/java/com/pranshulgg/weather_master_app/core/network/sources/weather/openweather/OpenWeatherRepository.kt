package com.pranshulgg.weather_master_app.core.network.sources.weather.openweather

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResultType
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityResult
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityResultType
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.network.sources.weather.metoffice.model.MetOfficeForecastJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo.OpenMeteoApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo.airquality.OpenMeteoAqiApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json.bundle.OpenWeatherJsonBundle
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isCurrentAirQualitySafe
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isWeatherCacheSafe
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnAirQualityCache
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnWeatherCache
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.mergeHourlyWeather
import com.pranshulgg.weather_master_app.data.local.dao.airquality.AirQualityDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.ApiKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.mapper.airquality.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.airquality.toEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.metoffice.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.openmeteo.airquality.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.openweather.airquality.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.openweather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.repository.data.AirQualityRepository
import com.pranshulgg.weather_master_app.data.repository.data.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import javax.inject.Inject


class OpenWeatherRepository @Inject constructor(
    val dao: LocationsDao,
    val weatherDao: WeatherDao,
    val api: OpenWeatherApi,
    val airQualityDao: AirQualityDao,
    val apiKeysDao: ApiKeysDao
) : WeatherRepository, AirQualityRepository {

    override val weatherSource = Source.OPEN_WEATHER
    override val airQualitySource = Source.OPEN_WEATHER

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
                WeatherResultType.SUCCESS -> return@withContext WeatherResult.Success(cache!!.toDomain())
                else -> {}
            }

            val isCacheSafe = isWeatherCacheSafe(cache)

            if (apiKey?.apiKey.isNullOrBlank()) {
                return@withContext WeatherResult.Error(
                    exception = AppException.NoApiKeyError(),
                    if (isCacheSafe) cache?.toDomain() else null
                )
            }


            return@withContext try {

                val current = safeApiCall {
                    api.fetchCurrent(
                        location.latitude, location.longitude, apiKey.apiKey
                    )
                }.getOrElse { return@withContext WeatherResult.Error(exception = it.toAppException()) }

                val forecast = safeApiCall {
                    api.fetchForecast(
                        location.latitude, location.longitude, apiKey.apiKey
                    )
                }.getOrElse { return@withContext WeatherResult.Error(exception = it.toAppException()) }

                val final = OpenWeatherJsonBundle(
                    current = current,
                    forecast = forecast
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
                    if (isCacheSafe) cache?.toDomain() else null
                )

            }

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
            AirQualityResultType.RETURN_CACHE -> return@withContext AirQualityResult.Success(cache!!.toDomain())
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
            }.getOrElse { return@withContext AirQualityResult.Error(exception = it.toAppException()) }


            val domain = airQuality.toDomain(location)

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