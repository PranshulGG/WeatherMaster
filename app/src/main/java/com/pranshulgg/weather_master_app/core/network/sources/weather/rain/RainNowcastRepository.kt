package com.pranshulgg.weather_master_app.core.network.sources.weather.rain

import android.util.Base64
import com.google.gson.Gson
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.weather.rain.RainNowcast
import com.pranshulgg.weather_master_app.core.model.weather.rain.RainNowcastPoint
import com.pranshulgg.weather_master_app.core.network.sources.weather.brightsky.BrightSkyRadarApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.buienradar.BuienradarGraphApi
import com.pranshulgg.weather_master_app.core.network.sources.weather.buienradar.BuienradarGraphApi.Companion.TYPE_MINUTE
import com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo.json.OpenMeteoMinutely15Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import java.util.zip.Inflater
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

/**
 * Builds the high-resolution (~3h) rain nowcast shown on the Rain block.
 *
 * Source priority:
 * 1. **Buienradar** graph API (5 min steps, radar extrapolation) — NL, BE and
 *    parts of western / northern DE. This is the same data that powers the
 *    graphs on buienradar.nl.
 * 2. **DWD Radarkomposit RV** via Bright Sky (5 min steps, radar extrapolation)
 *    — all of Germany.
 * 3. **Open-Meteo minutely_15** (15 min steps, high-res model) — global
 *    fallback, and used to extend the ~2h radar window to a full 3 hours.
 */
@Singleton
class RainNowcastRepository @Inject constructor(
    private val buienradarApi: BuienradarGraphApi,
    private val brightSkyRadarApi: BrightSkyRadarApi
) {

    companion object {
        /** Total nowcast window we aim for. */
        private const val WINDOW_MINUTES = 180L

        /** How much past radar context to include (bars before "now"). */
        private const val PAST_CONTEXT_MINUTES = 15L

        private const val PROVIDER_BUIENRADAR = "Buienradar"
        private const val PROVIDER_DWD = "DWD"
        private const val PROVIDER_OPEN_METEO = "Open-Meteo"

        /** DWD RV value scale: raw grid units are 0.01 mm per 5 minutes. */
        private const val DWD_MM_PER_UNIT = 0.01

        /** DWD RV grid pixel size (crop responses are always 401 px wide). */
        private const val DWD_GRID_WIDTH = 401
    }

    private val plainClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    /**
     * Returns the nowcast, or null when no high-resolution data is available
     * for this location (callers should then simply hide the section).
     */
    suspend fun getNowcast(location: Location): RainNowcast? {
        val now = Instant.now()

        val primary = when (location.countryCode?.uppercase()) {
            "DE" -> getDwdRadarNowcast(location, now) ?: getBuienradarNowcast(location, now)
            else -> getBuienradarNowcast(location, now) ?: getDwdRadarNowcast(location, now)
        } ?: return null

        return extendWithOpenMeteoTail(location, now, primary)
    }

    // ---------------------------------------------------------------------
    // Buienradar (NL / BE / parts of DE) — 5 minute rain rates in mm/h
    // ---------------------------------------------------------------------

    private suspend fun getBuienradarNowcast(
        location: Location,
        now: Instant
    ): RainNowcast? {
        return try {
            val start = now.minus(Duration.ofMinutes(PAST_CONTEXT_MINUTES))
            val end = now.plus(Duration.ofMinutes(WINDOW_MINUTES))

            val response = buienradarApi.fetchGeoForecast(
                type = TYPE_MINUTE,
                latitude = round2(location.latitude),
                longitude = round2(location.longitude),
                startUtc = formatUtc(start),
                endUtc = formatUtc(end)
            )

            val entries = response.body()?.forecasts ?: return null
            if (entries.isEmpty()) return null

            val points = entries.mapNotNull { entry ->
                val utc = entry.utcDateTime ?: return@mapNotNull null
                val value = entry.dataValue ?: return@mapNotNull null
                val time = try {
                    Instant.parse("${utc}Z")
                } catch (e: Exception) {
                    return@mapNotNull null
                }
                RainNowcastPoint(
                    time = time.toEpochMilli(),
                    intensityMmPerHour = value.coerceAtLeast(0.0),
                    intervalMinutes = 5
                )
            }

            if (points.isEmpty()) null else RainNowcast(
                points = points.sortedBy { it.time },
                providerName = PROVIDER_BUIENRADAR,
                isRadarBased = true
            )
        } catch (e: Exception) {
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

    // ---------------------------------------------------------------------
    // Open-Meteo minutely_15 — global fallback + tail to reach a full 3h
    // ---------------------------------------------------------------------

    private suspend fun extendWithOpenMeteoTail(
        location: Location,
        now: Instant,
        primary: RainNowcast
    ): RainNowcast {
        val lastRadarTime = primary.points.maxOfOrNull { it.time } ?: return primary
        val windowEnd = now.plus(Duration.ofMinutes(WINDOW_MINUTES)).toEpochMilli()

        // radar already reaches far enough?
        if (lastRadarTime >= windowEnd - TimeUnit.MINUTES.toMillis(15)) {
            return primary
        }

        val tail = fetchOpenMeteoMinutely15(location, now) ?: return primary
        val tailPoints = tail.filter { it.time > lastRadarTime && it.time < windowEnd }
        if (tailPoints.isEmpty()) return primary

        return primary.copy(
            points = primary.points + tailPoints,
            providerName = "${primary.providerName} + $PROVIDER_OPEN_METEO"
        )
    }

    private suspend fun fetchOpenMeteoMinutely15(
        location: Location,
        now: Instant
    ): List<RainNowcastPoint>? = withContext(Dispatchers.IO) {
        fetchOpenMeteoMinutely15Blocking(location, now)
    }

    private fun fetchOpenMeteoMinutely15Blocking(
        location: Location,
        now: Instant
    ): List<RainNowcastPoint>? {
        return try {
            val start = now.minus(Duration.ofMinutes(PAST_CONTEXT_MINUTES))
            val end = now.plus(Duration.ofMinutes(WINDOW_MINUTES))

            val url = "https://api.open-meteo.com/v1/forecast".toHttpUrl().newBuilder()
                .addQueryParameter("latitude", location.latitude.toString())
                .addQueryParameter("longitude", location.longitude.toString())
                .addQueryParameter("minutely_15", "precipitation")
                .addQueryParameter("forecast_minutely_15", "16")
                .addQueryParameter("past_minutely_15", "1")
                .addQueryParameter("timeformat", "unixtime")
                .addQueryParameter("timezone", "UTC")
                .build()

            val request = Request.Builder().url(url).build()
            val body = plainClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                response.body?.string() ?: return null
            }

            val parsed = gson.fromJson(body, OpenMeteoMinutely15Json::class.java)
            val block = parsed.minutely15 ?: return null
            val times = block.time ?: return null
            val precip = block.precipitation ?: return null

            val cutoffStart = start.toEpochMilli()
            val cutoffEnd = end.toEpochMilli()

            val points = mutableListOf<RainNowcastPoint>()
            for (i in times.indices) {
                val epochSeconds = times[i]?.trim()?.toLongOrNull() ?: continue
                val epochMilli = epochSeconds * 1000L
                if (epochMilli < cutoffStart || epochMilli > cutoffEnd) continue
                val value = (precip.getOrNull(i) ?: 0.0).coerceAtLeast(0.0)
                // minutely_15 precipitation is mm accumulated per 15 minutes
                points.add(
                    RainNowcastPoint(
                        time = epochMilli,
                        intensityMmPerHour = value * 4.0,
                        intervalMinutes = 15
                    )
                )
            }
            points
        } catch (e: Exception) {
            null
        }
    }

    private fun round2(value: Double): Double = (value * 100).roundToInt() / 100.0

    private fun formatUtc(instant: Instant): String {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .withZone(ZoneOffset.UTC)
            .format(instant)
    }

    private fun formatBrightSkyDate(instant: Instant): String {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:00xxx")
            .withZone(ZoneOffset.UTC)
            .format(instant)
    }
}