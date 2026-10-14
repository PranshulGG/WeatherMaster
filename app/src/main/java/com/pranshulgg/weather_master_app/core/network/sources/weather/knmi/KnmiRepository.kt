package com.pranshulgg.weather_master_app.core.network.sources.weather.knmi

import android.util.Log
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.rain.RainNowcast
import com.pranshulgg.weather_master_app.core.model.weather.rain.RainNowcastPoint
import com.pranshulgg.weather_master_app.core.network.sources.weather.rain.PROVIDER_KNMI
import com.pranshulgg.weather_master_app.core.network.sources.weather.rain.RAIN_NOWCAST_WINDOW_MINUTES
import com.pranshulgg.weather_master_app.core.network.sources.weather.rain.formatUtcWms
import com.pranshulgg.weather_master_app.data.repository.ApiKeysRepository
import com.pranshulgg.weather_master_app.data.repository.capability.AirQualityCapability
import com.pranshulgg.weather_master_app.data.repository.capability.AlertCapability
import com.pranshulgg.weather_master_app.data.repository.capability.NowcastCapability
import com.pranshulgg.weather_master_app.data.repository.capability.WeatherCapability
import com.pranshulgg.weather_master_app.data.repository.data.BaseRepository
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * KNMI repository.
 *
 * The KNMI Dataplatform publishes no point-forecast feed this app could use
 * as a weather source; what KNMI adds here is the high-resolution rain radar
 * nowcast (dataset `radar_forecast_2.0`, layer `precipitation_nowcast`) via
 * its ADAGUC WMS GetFeatureInfo endpoint: 5-minute radar extrapolation for a
 * 2 hour window over the Netherlands. So this repository implements ONLY the
 * nowcast capability — the `Source.KNMI` entry correspondingly declares
 * `Capability.RAIN_NOWCAST` and nothing else.
 *
 * Anonymous access works (shared per-IP quota); an optional user API key
 * (Settings -> Weather Sources -> API key config) is sent as the
 * Authorization header against the keyed host for a personal quota.
 */
class KnmiRepository @Inject constructor(
    private val apiKeysRepository: ApiKeysRepository
) : BaseRepository() {

    override val weatherSource = Source.NONE
    override val alertSource = Source.NONE
    override val airQualitySource = Source.NONE
    override val nowcastSource = Source.KNMI

    override fun weatherCapability(): WeatherCapability? = null
    override fun alertCapability(): AlertCapability? = null
    override fun airQualityCapability(): AirQualityCapability? = null

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    override fun nowcastCapability(): NowcastCapability? {
        return object : NowcastCapability {

            // The nowcast grid covers the Netherlands; the WMS point query
            // simply returns no value outside it, so decide by geography (the
            // country code is not reliably populated for all locations).
            override fun covers(location: Location): Boolean =
                location.countryCode?.uppercase() == "NL" ||
                    (location.latitude in BBOX_MIN_LAT..BBOX_MAX_LAT &&
                        location.longitude in BBOX_MIN_LON..BBOX_MAX_LON)

            override suspend fun fetchNowcast(
                location: Location,
                now: Instant
            ): RainNowcast? = fetchNowcastFromWms(location, now)
        }
    }

    // ---------------------------------------------------------------------
    // radar_forecast_2.0 via WMS GetFeatureInfo — NL, 5 min, 2h
    // ---------------------------------------------------------------------

    /**
     * WMS request geometry: full NL bounding box + request raster size.
     */
    private companion object {
        const val TAG = "RainNowcast"
        const val WMS_BBOX = "3.0,50.5,7.5,54.0"
        const val WMS_WIDTH = 900
        const val WMS_HEIGHT = 700
        const val BBOX_MIN_LON = 3.0
        const val BBOX_MAX_LON = 7.5
        const val BBOX_MIN_LAT = 50.5
        const val BBOX_MAX_LAT = 54.0
    }

    private suspend fun fetchNowcastFromWms(
        location: Location,
        now: Instant
    ): RainNowcast? = withContext(Dispatchers.IO) {
        try {
            // Optional KNMI key: stored in the same ApiKeys DB the app uses
            // for other sources (Settings -> Weather Sources -> API key config).
            // Empty -> anonymous WMS host (shared per-IP quota).
            val apiKey = apiKeysRepository
                .getKeyForSource(Source.KNMI)
                ?.trim().orEmpty()
            val useKey = apiKey.isNotEmpty()
            val wmsUrl = (if (useKey) "https://api.dataplatform.knmi.nl" else "https://anonymous.api.dataplatform.knmi.nl") +
                "/wms/adaguc-server"

            // Latest cycle: snap to the current 5-min boundary first (the WMS
            // layer only has exact 5-min timesteps; sub-second precision makes
            // the server answer "store has no results"), then probe back up to
            // 1h in 5-min steps because the newest cycle can land late.
            val snappedNow = Instant.ofEpochSecond(
                now.epochSecond - now.epochSecond % (5 * 60)
            )

            fun featureInfoUrl(timeUtc: Instant): Request.Builder {
                val url = wmsUrl.toHttpUrl().newBuilder()
                    .addQueryParameter("DATASET", "radar_forecast_2.0")
                    .addQueryParameter("SERVICE", "WMS")
                    .addQueryParameter("VERSION", "1.1.1")
                    .addQueryParameter("REQUEST", "GetFeatureInfo")
                    .addQueryParameter("LAYERS", "precipitation_nowcast")
                    .addQueryParameter("QUERY_LAYERS", "precipitation_nowcast")
                    .addQueryParameter("STYLES", "rainrate-blue-to-purple/nearest")
                    .addQueryParameter("SRS", "EPSG:4326")
                    .addQueryParameter("BBOX", WMS_BBOX)
                    .addQueryParameter("WIDTH", WMS_WIDTH.toString())
                    .addQueryParameter("HEIGHT", WMS_HEIGHT.toString())
                    .addQueryParameter("INFO_FORMAT", "text/html")
                    .addQueryParameter("FEATURE_COUNT", "1")
                    .addQueryParameter(
                        "X",
                        (((location.longitude - BBOX_MIN_LON) / (BBOX_MAX_LON - BBOX_MIN_LON)) * WMS_WIDTH).toInt()
                            .toString()
                    )
                    .addQueryParameter(
                        "Y",
                        (((BBOX_MAX_LAT - location.latitude) / (BBOX_MAX_LAT - BBOX_MIN_LAT)) * WMS_HEIGHT).toInt()
                            .toString()
                    )
                    .addQueryParameter("TIME", formatUtcWms(timeUtc))
                    .build()
                val b = Request.Builder().url(url)
                if (useKey) b.header("Authorization", apiKey)
                return b
            }

            var cycleStart: Instant? = null
            var back = 0L
            while (back <= 60 && cycleStart == null) {
                val candidate = snappedNow.minusSeconds(back * 60)
                val response = client.newCall(featureInfoUrl(candidate).build()).execute()
                response.use { r ->
                    val body = r.body?.string().orEmpty()
                    if (r.isSuccessful && body.contains("forecast")) {
                        cycleStart = candidate
                    }
                }
                back += 5
            }
            cycleStart ?: return@withContext null

            val points = mutableListOf<RainNowcastPoint>()
            for (step in 0..(RAIN_NOWCAST_WINDOW_MINUTES / 5).toInt()) {
                val t = cycleStart.plusSeconds(step * 5L * 60)
                val response = client.newCall(featureInfoUrl(t).build()).execute()
                response.use { r ->
                    val body0 = r.body?.string().orEmpty()
                    if (!r.isSuccessful) {
                        Log.w(TAG, "KNMI step +${step * 5}min HTTP ${r.code}")
                        return@use
                    }
                    // The GFI response is a tiny HTML table:
                    // "<td>forecast</td><td><b>0.12</b></td><td> mm/hr</td>"
                    // — strip all tags first, then read "forecast <num> mm/hr".
                    val m = Regex("forecast\\s+([0-9.]+)\\s*mm/hr")
                        .find(body0.replace(Regex("<[^>]*>"), " "))
                        ?: return@use
                    val value = m.groupValues[1].toDoubleOrNull() ?: return@use
                    points.add(
                        RainNowcastPoint(
                            time = t.toEpochMilli(),
                            intensityMmPerHour = value.coerceAtLeast(0.0),
                            intervalMinutes = 5
                        )
                    )
                }
            }

            if (points.isEmpty()) Log.w(TAG, "KNMI nowcast EMPTY (cycle=$cycleStart parsed=0)")

            RainNowcast(
                points = points.sortedBy { it.time },
                providerName = PROVIDER_KNMI,
                isRadarBased = true
            )
        } catch (e: Exception) {
            Log.w(TAG, "KNMI nowcast FAILED: ${e.javaClass.simpleName}: ${e.message}")
            null
        }
    }
}