package com.pranshulgg.weather_master_app.core.network.sources.weather.openweather

import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
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
import javax.inject.Inject


/**
 * One Call 4.0 auto-detect (dew point, UV index, cloud cover) implemented by https://github.com/reveler-hub
 */
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

    override fun weatherCapability(): WeatherCapability {
        return object : WeatherCapability {

            override suspend fun fetchAndProcess(
                location: Location,
                isManualRefresh: Boolean,
                isForceRefresh: Boolean,
                cacheModel: CacheModel
            ): WeatherDataPack {
                val apiKeyValue = cacheModel.apiKey!!
                val apiKeyEntity = apiKeysDao.getApiKeyForSource(location.source)

                val now = System.currentTimeMillis()
                val checkedAt = apiKeyEntity?.oneCallV4CheckedAt
                val oneCallCooldownActive = apiKeyEntity?.oneCallV4Access == false &&
                        checkedAt != null &&
                        (now - checkedAt) < ONE_CALL_V4_RECHECK_INTERVAL_MS

                val domain = if (!oneCallCooldownActive) {
                    val oneCallCurrent = safeApiCall {
                        oneCallApi.fetchCurrent(
                            location.latitude, location.longitude, apiKeyValue
                        )
                    }

                    val currentJson = oneCallCurrent.getOrNull()
                    if (currentJson != null) {
                        apiKeysDao.updateOneCallV4Access(location.source, true, now)

                        val startEpochSeconds = now / 1000

                        val hourly = safeApiCall {
                            oneCallApi.fetchHourly(
                                location.latitude,
                                location.longitude,
                                ONE_CALL_V4_HOURLY_COUNT,
                                startEpochSeconds,
                                apiKeyValue
                            )
                        }.getOrThrow()

                        val daily = safeApiCall {
                            oneCallApi.fetchDaily(
                                location.latitude,
                                location.longitude,
                                ONE_CALL_V4_DAILY_COUNT,
                                startEpochSeconds,
                                apiKeyValue
                            )
                        }.getOrThrow()

                        OpenWeatherOneCallJsonBundle(
                            current = currentJson,
                            hourly = hourly,
                            daily = daily
                        ).toDomain(location)
                    } else {
                        apiKeysDao.updateOneCallV4Access(location.source, false, now)
                        fetchLegacyWeather(location, apiKeyValue)
                    }
                } else {
                    fetchLegacyWeather(location, apiKeyValue)
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

    /**
     * One Call 4.0 requires a separate "One Call by Call" subscription on top of a plain
     * API key. When the key isn't subscribed, fetchCurrent() fails (401) and we fall back
     * here, using the legacy free /data/2.5/ endpoints instead.
     */
    private suspend fun fetchLegacyWeather(location: Location, apiKey: String): Weather {
        val current = safeApiCall {
            api.fetchCurrent(location.latitude, location.longitude, apiKey)
        }.getOrThrow()

        val forecast = safeApiCall {
            api.fetchForecast(location.latitude, location.longitude, apiKey)
        }.getOrThrow()

        return OpenWeatherJsonBundle(current = current, forecast = forecast).toDomain(location)
    }

    companion object {
        // Re-probe One Call 4.0 access this often after a denial, in case the user
        // subscribes mid-session - keeps us from burning a call on every refresh.
        private const val ONE_CALL_V4_RECHECK_INTERVAL_MS = 2 * 60 * 60 * 1000L

        private const val ONE_CALL_V4_HOURLY_COUNT = 48
        private const val ONE_CALL_V4_DAILY_COUNT = 8
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