package com.pranshulgg.weather_master_app.core.network.sources.weather.rain

import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.weather.rain.RainNowcast
import com.pranshulgg.weather_master_app.core.model.weather.rain.RainNowcastPoint
import com.pranshulgg.weather_master_app.core.network.sources.weather.brightsky.BrightSkyRadarApi
import com.pranshulgg.weather_master_app.core.prefs.helper.PreferencesHelper
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import java.util.zip.Inflater
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

/**
 * Builds the high-resolution (2h) rain nowcast shown on the Rain block.
 *
 * Source priority:
 * 1. **KNMI radar_forecast_2.0** via WMS GetFeatureInfo (5 min steps, +0…+120
 *    min, radar extrapolation) — the Netherlands. Anonymous access works;
 *    a user-provided API key (settings) is sent as the Authorization header
 *    to the keyed host for a personal quota.
 * 2. **DWD Radarkomposit RV** via Bright Sky (5 min steps, radar
 *    extrapolation) — all of Germany (~2h window, no tail stitching).
 *
 * Both sources are CC BY 4.0 / open-data with attribution; the providerName
 * on the returned nowcast is what the app and the widget display.
 */
@Singleton
class RainNowcastRepository @Inject constructor(
    private val brightSkyRadarApi: BrightSkyRadarApi
) {

    companion object {
        /** KNMI nowcast length: 25 steps (+0 … +120 min), 5-min resolution. */
        const val WINDOW_MINUTES = 120L

        /** How much past radar context to include (bars before "now"). */
        private const val PAST_CONTEXT_MINUTES = 15L

        const val PROVIDER_KNMI = "KNMI"
        const val PROVIDER_DWD = "DWD"

        /** DWD RV value scale: raw grid units are 0.01 mm per 5 minutes. */
        private const val DWD_MM_PER_UNIT = 0.01

        /** DWD RV grid pixel size (crop responses are always 401 px wide). */
        private const val DWD_GRID_WIDTH = 401

        private const val TAG = "RainNowcast"

        /** WMS request geometry: full NL bounding box + request raster size. */
        private const val WMS_BBOX = "3.0,50.5,7.5,54.0"
        private const val WMS_WIDTH = 900
        private const val WMS_HEIGHT = 700
        private const val BBOX_MIN_LON = 3.0
        private const val BBOX_MAX_LON = 7.5
        private const val BBOX_MIN_LAT = 50.5
        private const val BBOX_MAX_LAT = 54.0

        /** Settings key for the user's KNMI API key (empty = anonymous). */
        const val PREF_KNMI_API_KEY = "knmiApiKey"
    }

    private val plainClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    /**
     * Returns the nowcast, or null when no high-resolution data is available
     * for this location (callers should then simply hide the section /
     * fall back to hourly bars).
     */
    suspend fun getNowcast(location: Location): RainNowcast? {
        val now = Instant.now()

        // KNMI's nowcast grid covers the Netherlands; the WMS point query
        // simply returns no value outside it, so decide by geography (the
        // country code is not reliably populated for all locations).
        val inNl = location.countryCode?.uppercase() == "NL" ||
            (location.latitude in 50.5..54.0 && location.longitude in 3.0..7.5)

        val primary = if (inNl) {
            getKnmiNowcast(location, now) ?: getDwdRadarNowcast(location, now).also {
                if (it != null) Log.w(TAG, "KNMI failed/empty -> DWD fallback used")
            }
        } else {
            getDwdRadarNowcast(location, now) ?: getKnmiNowcast(location, now)
        }
        return primary
    }

    // ---------------------------------------------------------------------
    // KNMI radar_forecast_2.0 via WMS GetFeatureInfo — NL, 5 min, 2h
    // ---------------------------------------------------------------------

    private suspend fun getKnmiNowcast(
        location: Location,
        now: Instant
    ): RainNowcast? = withContext(Dispatchers.IO) {
        try {
            val apiKey = PreferencesHelper.getString(PREF_KNMI_API_KEY)?.trim().orEmpty()
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
                    .addQueryParameter("TIME", formatUtc(timeUtc))
                    .build()
                val b = Request.Builder().url(url)
                if (useKey) b.header("Authorization", apiKey)
                return b
            }

            var cycleStart: Instant? = null
            var back = 0L
            while (back <= 60 && cycleStart == null) {
                val candidate = snappedNow.minusSeconds(back * 60)
                val response = plainClient.newCall(featureInfoUrl(candidate).build()).execute()
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
            for (step in 0..(WINDOW_MINUTES / 5).toInt()) {
                val t = cycleStart.plusSeconds(step * 5L * 60)
                val response = plainClient.newCall(featureInfoUrl(t).build()).execute()
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

    // ---------------------------------------------------------------------
    // DWD Radarkomposit RV via Bright Sky — 5 minute rain rates, all of DE
    // ---------------------------------------------------------------------

    private suspend fun getDwdRadarNowcast(
        location: Location,
        now: Instant
    ): RainNowcast? {
        return try {
            val start = now.minus(Duration.ofMinutes(PAST_CONTEXT_MINUTES))

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
                val raw = extractGridValue(encoded, cropWidth, ix, iy) ?: return@mapNotNull null
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
            null
        }
    }

    /**
     * Decompresses one DWD radar frame (base64 + zlib int16 little-endian
     * grid) and returns the unsigned value at grid position ([ix], [iy]).
     */
    private fun extractGridValue(base64Zlib: String, width: Int, ix: Int, iy: Int): Double? {
        return try {
            val compressed = Base64.decode(base64Zlib, Base64.NO_WRAP)
            val inflated = inflate(compressed) ?: return null

            val count = inflated.size / 2
            if (count <= 0) return null

            val index = iy * width + ix
            if (index < 0 || index >= count) return null

            val lo = inflated[index * 2].toInt() and 0xFF
            val hi = inflated[index * 2 + 1].toInt() and 0xFF
            (hi shl 8 or lo).toDouble()
        } catch (e: Exception) {
            null
        }
    }

    private fun inflate(data: ByteArray): ByteArray? {
        return try {
            val inflater = Inflater()
            inflater.setInput(data)
            val output = ByteArrayOutputStream(data.size * 4)
            val buffer = ByteArray(8192)
            while (!inflater.finished()) {
                val n = inflater.inflate(buffer)
                if (n == 0 && inflater.needsInput()) break
                output.write(buffer, 0, n)
            }
            inflater.end()
            output.toByteArray()
        } catch (e: Exception) {
            null
        }
    }

    private fun formatBrightSkyDate(instant: Instant): String {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:00xxx")
            .withZone(java.time.ZoneOffset.UTC)
            .format(instant)
    }

    private fun formatUtc(instant: Instant): String {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .withZone(java.time.ZoneOffset.UTC)
            .format(instant)
    }
}