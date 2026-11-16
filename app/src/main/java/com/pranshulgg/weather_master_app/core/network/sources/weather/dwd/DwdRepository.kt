package com.pranshulgg.weather_master_app.core.network.sources.weather.dwd

import android.util.Log
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.FinishedWeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherDataPack
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.network.sources.weather.brightsky.BrightSkyRadarApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.dwd.json.bundle.DwdWeatherJsonBundle
import com.pranshulgg.weather_master_app.core.network.sources.weather.rain.DWD_MM_PER_UNIT
import com.pranshulgg.weather_master_app.core.network.sources.weather.rain.PROVIDER_DWD
import com.pranshulgg.weather_master_app.core.network.sources.weather.rain.RAIN_NOWCAST_PAST_CONTEXT_MINUTES
import com.pranshulgg.weather_master_app.core.model.weather.rain.RainNowcast
import com.pranshulgg.weather_master_app.core.model.weather.rain.RainNowcastPoint
import com.pranshulgg.weather_master_app.core.network.sources.weather.rain.extractDwdGridValue
import com.pranshulgg.weather_master_app.core.network.sources.weather.rain.formatBrightSkyDate
import com.pranshulgg.weather_master_app.core.utils.formatters.safeZoneId
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.dwd.toDomain
import com.pranshulgg.weather_master_app.data.repository.capability.AirQualityCapability
import com.pranshulgg.weather_master_app.data.repository.capability.AlertCapability
import com.pranshulgg.weather_master_app.data.repository.capability.NowcastCapability
import com.pranshulgg.weather_master_app.data.repository.capability.WeatherCapability
import com.pranshulgg.weather_master_app.data.repository.data.BaseRepository
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import javax.inject.Inject
import kotlin.math.roundToInt

class DwdRepository @Inject constructor(
    val dao: WeatherContextDao,
    val weatherDao: WeatherDao,
    val api: DwdApi,
    private val brightSkyRadarApi: BrightSkyRadarApi
) : BaseRepository() {

    override val weatherSource = Source.DWD
    override val alertSource = Source.NONE
    override val airQualitySource = Source.NONE
    override val nowcastSource = Source.DWD

    override fun weatherCapability(): WeatherCapability? {
        return object : WeatherCapability {
            override suspend fun fetchAndProcess(
                location: Location,
                isManualRefresh: Boolean,
                isForceRefresh: Boolean,
                cacheModel: CacheModel
            ): WeatherDataPack {
                val response = safeApiCall {
                    api.fetchCurrentWeather(
                        location.latitude,
                        location.longitude
                    )
                }.getOrThrow()
                val dates = getStartEndDate(location)

                val forecastResponse = safeApiCall {
                    api.fetchWeatherForecast(
                        location.latitude, location.longitude, dates.first, dates.second
                    )
                }.getOrThrow()


                val final = DwdWeatherJsonBundle(
                    current = response,
                    forecastJson = forecastResponse
                )

                val domain = final.toDomain(location)

                return WeatherDataPack(
                    weather = domain
                )
            }

            override suspend fun saveToDb(data: WeatherDataPack, cacheModel: CacheModel) {
                useGenericSaveImplementationForWeather(
                    cacheModel.cachedHourly,
                    data.weather,
                    weatherDao
                )
            }

            override fun finishedResult(data: Weather): FinishedWeatherResult {
                return FinishedWeatherResult(weather = data)
            }
        }
    }

    override fun airQualityCapability(): AirQualityCapability? = null
    override fun alertCapability(): AlertCapability? = null

    /**
     * Rain nowcast capability: the DWD "Radarkomposit RV" radar product
     * served via Bright Sky (5-minute frames, observed + extrapolated).
     * Coverage is all of Germany; Bright Sky rejects points outside it.
     */
    override fun nowcastCapability(): NowcastCapability? {
        return object : NowcastCapability {

            override fun covers(location: Location): Boolean =
                location.countryCode?.uppercase() == "DE" ||
                    (location.latitude in DWD_BBOX_MIN_LAT..DWD_BBOX_MAX_LAT &&
                        location.longitude in DWD_BBOX_MIN_LON..DWD_BBOX_MAX_LON)

            override suspend fun fetchNowcast(
                location: Location,
                now: Instant
            ): RainNowcast? = getDwdRadarNowcast(location, now)
        }
    }

    private companion object {
        /** Approximate German coverage boundary of the DWD radar composite. */
        const val DWD_BBOX_MIN_LAT = 47.3
        const val DWD_BBOX_MAX_LAT = 55.1
        const val DWD_BBOX_MIN_LON = 5.9
        const val DWD_BBOX_MAX_LON = 15.0
        const val TAG = "RainNowcast"
    }

    private suspend fun getDwdRadarNowcast(
        location: Location,
        now: Instant
    ): RainNowcast? {
        return try {
            val start = now.minus(Duration.ofMinutes(RAIN_NOWCAST_PAST_CONTEXT_MINUTES))

            val response = brightSkyRadarApi.fetchRadar(
                lat = location.latitude,
                lon = location.longitude,
                date = formatBrightSkyDate(start),
                maxLat = location.latitude + BrightSkyRadarApi.CROP_HALF_DEGREES,
                minLat = location.latitude - BrightSkyRadarApi.CROP_HALF_DEGREES,
                maxLon = location.longitude + BrightSkyRadarApi.CROP_HALF_DEGREES,
                minLon = location.longitude - BrightSkyRadarApi.CROP_HALF_DEGREES
            )

            val body = response.body() ?: return null
            val frames = body.radar ?: return null
            val bbox = body.bbox ?: return null
            val position = body.latlon_position ?: return null
            val px = position.x ?: return null
            val py = position.y ?: return null
            if (bbox.size < 4) return null

            // bbox = [cropX, cropY, cropX + width, cropY + height] in composite pixels
            val cropWidth = bbox[2] - bbox[0]

            // latlon_position is already relative to the crop origin
            val ix = px.roundToInt()
            val iy = py.roundToInt()

            val points = frames.mapNotNull { frame ->
                val encoded = frame.precipitation_5 ?: return@mapNotNull null
                val timestamp = frame.timestamp ?: return@mapNotNull null
                val time = try {
                    OffsetDateTime.parse(timestamp).toInstant()
                } catch (e: Exception) {
                    return@mapNotNull null
                }
                val raw = extractDwdGridValue(encoded, cropWidth, ix, iy) ?: return@mapNotNull null
                RainNowcastPoint(
                    time = time.toEpochMilli(),
                    intensityMmPerHour = raw * DWD_MM_PER_UNIT * 12.0, // 0.01mm/5min -> mm/h
                    intervalMinutes = 5
                )
            }

            if (points.isEmpty()) null else RainNowcast(
                points = points.sortedBy { it.time },
                providerName = PROVIDER_DWD,
                isRadarBased = true
            )
        } catch (e: Exception) {
            Log.w(TAG, "DWD radar nowcast failed: ${e.message}")
            null
        }
    }
}

private fun getStartEndDate(location: Location): Pair<String, String> {
    val zoneId = safeZoneId(location.timezone)

    val start = LocalDate.now(zoneId)
    val end = start.plusDays(5)

    return Pair(start.toString(), end.toString())
}

