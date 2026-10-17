package com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo

import android.util.Log
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
import com.pranshulgg.weather_master_app.core.model.weather.rain.RainNowcast
import com.pranshulgg.weather_master_app.core.model.weather.rain.RainNowcastPoint
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo.airquality.OpenMeteoAqiApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.rain.PROVIDER_OPEN_METEO
import com.pranshulgg.weather_master_app.core.network.sources.weather.rain.RAIN_NOWCAST_PAST_CONTEXT_MINUTES
import com.pranshulgg.weather_master_app.core.network.sources.weather.rain.RAIN_NOWCAST_WINDOW_MINUTES
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isCurrentAirQualitySafe
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnAirQualityCache
import com.pranshulgg.weather_master_app.data.local.dao.airquality.AirQualityDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.openmeteo.airquality.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.airquality.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.airquality.toEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.openmeteo.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.repository.airquality.AirQualityCacheModel
import com.pranshulgg.weather_master_app.data.repository.capability.AirQualityCapability
import com.pranshulgg.weather_master_app.data.repository.capability.AlertCapability
import com.pranshulgg.weather_master_app.data.repository.capability.NowcastCapability
import com.pranshulgg.weather_master_app.data.repository.capability.WeatherCapability
import com.pranshulgg.weather_master_app.data.repository.data.BaseRepository
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

private const val TAG = "RainNowcast"

class OpenMeteoRepository @Inject constructor(
    val dao: WeatherContextDao,
    val weatherDao: WeatherDao,
    val api: OpenMeteoApi,
    val airQualityApi: OpenMeteoAqiApi,
    val airQualityDao: AirQualityDao
) : BaseRepository() {

    override val weatherSource = Source.OPEN_METEO
    override val airQualitySource = Source.OPEN_METEO
    override val alertSource = Source.NONE


    override fun weatherCapability(): WeatherCapability {
        return object : WeatherCapability {

            override suspend fun fetchAndProcess(
                location: Location,
                isManualRefresh: Boolean,
                isForceRefresh: Boolean,
                cacheModel: CacheModel
            ): WeatherDataPack {

                val response = safeApiCall {
                    api.fetchWeather(
                        location.latitude,
                        location.longitude,
                        location.timezone,
                        model = location.openMeteoModel.modelId
                    )
                }.getOrThrow()

                val domain = response.toDomain(location)

                return WeatherDataPack(domain)
            }

            override suspend fun saveToDb(data: WeatherDataPack, cacheModel: CacheModel) {
                weatherDao.insertWeather(
                    data.weather.current.toCurrentWeatherEntity(data.weather.location.id),
                    data.weather.hourly.toHourlyWeatherEntity(data.weather.location),
                    data.weather.daily.toDailyWeatherEntity(data.weather.location.id),
                    data.weather.location.id
                )
            }

            override fun finishedResult(data: Weather): FinishedWeatherResult {
                return FinishedWeatherResult(weather = data)
            }
        }
    }


    override fun airQualityCapability(): AirQualityCapability? {
        return object : AirQualityCapability {
            override suspend fun fetchAndProcess(
                location: Location,
                isManualRefresh: Boolean,
                isForceRefresh: Boolean,
                airQualityCacheModel: AirQualityCacheModel
            ): AirQualityDataPack {
                val response = safeApiCall {
                    airQualityApi.fetchAirQuality(location.latitude, location.longitude)
                }.getOrThrow()


                return AirQualityDataPack(airQuality = response.toDomain(), location)
            }

            override suspend fun saveToDb(data: AirQualityDataPack) {
                useGenericSaveImplementationForAirQuality(airQualityDao, data)
            }

            override fun finishedResult(data: AirQuality): FinishedAirQualityResult {
                return FinishedAirQualityResult(airQuality = data)
            }
        }
    }

    override fun alertCapability(): AlertCapability? = null

    /**
     * Rain nowcast capability: Open-Meteo's minutely-15 model precipitation,
     * keyless and worldwide — lower fidelity than the KNMI/DWD radar feeds
     * (15-minute model steps instead of 5-minute radar extrapolation), so it
     * is the candidate that keeps the nowcast available everywhere.
     */
    override val nowcastSource = Source.OPEN_METEO

    override fun nowcastCapability(): NowcastCapability? {
        return object : NowcastCapability {

            override fun covers(location: Location): Boolean = true

            override suspend fun fetchNowcast(
                location: Location,
                now: Instant
            ): RainNowcast? {
                return try {
                    val response = api.fetchMinutely15Precipitation(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                    val body = response.body() ?: return null
                    val block = body.minutely15 ?: return null
                    val times = block.time ?: return null
                    val values = block.precipitation ?: return null

                    // minutely_15 values are mm accumulated over each
                    // 15-minute interval (unit=mm default for the
                    // precipitation variable). The nowcast pipeline works in
                    // mm/h: convert per interval.
                    val windowStart = now.minus(Duration.ofMinutes(RAIN_NOWCAST_PAST_CONTEXT_MINUTES))
                    val windowEnd = now.plus(Duration.ofMinutes(RAIN_NOWCAST_WINDOW_MINUTES))

                    val points = mutableListOf<RainNowcastPoint>()
                    for (i in times.indices) {
                        val t = times[i] * 1000L // epoch seconds -> millis (unixtime)
                        val valEnd = t + 15 * 60000L
                        if (valEnd <= windowStart.toEpochMilli() || t >= windowEnd.toEpochMilli()) continue
                        val mm = values.getOrNull(i) ?: continue
                        points.add(
                            RainNowcastPoint(
                                time = t,
                                // mm per 15 min -> mm/h
                                intensityMmPerHour = mm.coerceAtLeast(0.0) * 4.0,
                                intervalMinutes = 15
                            )
                        )
                    }
                    if (points.isEmpty()) null else RainNowcast(
                        points = points.sortedBy { it.time },
                        providerName = PROVIDER_OPEN_METEO,
                        isRadarBased = false
                    )
                } catch (e: Exception) {
                    Log.w(TAG, "Open-Meteo minutely15 nowcast failed: ${e.message}")
                    null
                }
            }
        }
    }
}