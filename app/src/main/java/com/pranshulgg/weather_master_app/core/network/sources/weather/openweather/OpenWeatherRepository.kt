package com.pranshulgg.weather_master_app.core.network.sources.weather.openweather

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.FinishedWeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherDataPack
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityDataPack
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityResult
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityResultType
import com.pranshulgg.weather_master_app.core.model.weather.airquality.FinishedAirQualityResult
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json.bundle.OpenWeatherJsonBundle
import com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json.bundle.OpenWeatherOneCallJsonBundle
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isCurrentAirQualitySafe
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnAirQualityCache
import com.pranshulgg.weather_master_app.data.local.dao.airquality.AirQualityDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.ApiKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.mapper.airquality.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.airquality.toEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.openweather.airquality.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.openweather.toDomain
import com.pranshulgg.weather_master_app.data.repository.airquality.AirQualityCacheModel
import com.pranshulgg.weather_master_app.data.repository.capability.AirQualityCapability
import com.pranshulgg.weather_master_app.data.repository.capability.AlertCapability
import com.pranshulgg.weather_master_app.data.repository.capability.WeatherCapability
import com.pranshulgg.weather_master_app.data.repository.data.BaseRepository
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class OpenWeatherRepository @Inject constructor(
    val dao: WeatherContextDao,
    val weatherDao: WeatherDao,
    val api: OpenWeatherApi,
    val oneCallApi: OpenWeatherOneCallApi,
    val airQualityDao: AirQualityDao,
    val apiKeysDao: ApiKeysDao
) : BaseRepository() {

    override val weatherSource = Source.OPEN_WEATHER
    override val airQualitySource = Source.OPEN_WEATHER
    override val alertSource = Source.NONE

    companion object {
        private const val ONE_CALL_V4_HOURLY_COUNT = 48
        private const val ONE_CALL_V4_DAILY_COUNT = 8
    }

    override fun weatherCapability(): WeatherCapability {
        return object : WeatherCapability {

            override suspend fun fetchAndProcess(
                location: Location,
                isManualRefresh: Boolean,
                isForceRefresh: Boolean,
                cacheModel: CacheModel
            ): WeatherDataPack {

                /**
                 * API key may be stored with or without "&one_call_4" suffix
                 * depending on which version the user wants to use
                 * We use this to determine wether to use OneCall 4.0 or free version
                 */

                val containsSuffix = cacheModel.apiKey!!.contains("&")

                val key = if (containsSuffix) cacheModel.apiKey.split("&")
                else listOf(cacheModel.apiKey, "")

                val resolvedKey = key.first()
                val isOneCall4 = key[1] == "one_call_4"
                val now = System.currentTimeMillis()

                val domain = if (isOneCall4) {
                    val oneCallCurrent = safeApiCall {
                        oneCallApi.fetchCurrent(
                            location.latitude, location.longitude, resolvedKey
                        )
                    }

                    val startEpochSeconds = now / 1000

                    val currentJson = oneCallCurrent.getOrNull()
                    if (currentJson != null) {
                        val hourly = safeApiCall {
                            oneCallApi.fetchHourly(
                                location.latitude,
                                location.longitude,
                                ONE_CALL_V4_HOURLY_COUNT,
                                startEpochSeconds,
                                resolvedKey
                            )
                        }.getOrThrow()

                        val daily = safeApiCall {
                            oneCallApi.fetchDaily(
                                location.latitude,
                                location.longitude,
                                ONE_CALL_V4_DAILY_COUNT,
                                startEpochSeconds,
                                resolvedKey
                            )
                        }.getOrThrow()

                        OpenWeatherOneCallJsonBundle(
                            current = currentJson,
                            hourly = hourly,
                            daily = daily
                        ).toDomain(location)
                    } else {
                        useFreeVersion(location, resolvedKey)
                    }

                } else {
                    useFreeVersion(location, resolvedKey)
                }


                return WeatherDataPack(domain)
            }

            override suspend fun saveToDb(data: WeatherDataPack, cacheModel: CacheModel) {
                useGenericSaveImplementationForWeather(
                    existingHourly = cacheModel.cachedHourly,
                    data.weather,
                    weatherDao
                )
            }

            override fun finishedResult(data: Weather): FinishedWeatherResult {
                return FinishedWeatherResult(weather = data)
            }
        }
    }

    private suspend fun useFreeVersion(location: Location, apiKey: String): Weather {
        val current = safeApiCall {
            api.fetchCurrent(location.latitude, location.longitude, apiKey)
        }.getOrThrow()

        val forecast = safeApiCall {
            api.fetchForecast(location.latitude, location.longitude, apiKey)
        }.getOrThrow()

        return OpenWeatherJsonBundle(current = current, forecast = forecast).toDomain(location)
    }

    override fun airQualityCapability(): AirQualityCapability {
        return object : AirQualityCapability {
            override suspend fun fetchAndProcess(
                location: Location,
                isManualRefresh: Boolean,
                isForceRefresh: Boolean,
                airQualityCacheModel: AirQualityCacheModel
            ): AirQualityDataPack {
                val airQuality = safeApiCall {
                    api.fetchAirQuality(
                        location.latitude,
                        location.longitude,
                        airQualityCacheModel.apiKey!!
                    )
                }.getOrThrow()

                return AirQualityDataPack(airQuality = airQuality.toDomain(location), location)
            }

            override suspend fun saveToDb(
                data: AirQualityDataPack
            ) {
                useGenericSaveImplementationForAirQuality(airQualityDao, data)
            }

            override fun finishedResult(data: AirQuality): FinishedAirQualityResult {
                return FinishedAirQualityResult(airQuality = data)
            }
        }
    }

    override fun alertCapability(): AlertCapability? = null
}