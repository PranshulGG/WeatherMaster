package com.pranshulgg.weather_master_app.core.network.sources.weather.rain

import android.util.Base64
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.zip.Inflater

/**
 * Shared, source-agnostic pieces of the rain nowcast pipeline. The coverage
 * and fetching logic per provider lives in the provider's own repository
 * (e.g. [com.pranshulgg.weather_master_app.core.network.sources.weather.knmi.KnmiRepository]);
 * only names, units and grid decoding are shared here.
 */

/** Nowcast window: 25 steps (+0 … +120 min), 5-min resolution. */
const val RAIN_NOWCAST_WINDOW_MINUTES = 120L

/** How much past radar context the fetches include (bars before "now"). */
const val RAIN_NOWCAST_PAST_CONTEXT_MINUTES = 15L

const val PROVIDER_KNMI = "KNMI"
const val PROVIDER_DWD = "DWD"

/**
 * Keyless model provider (Open-Meteo minutely_15, CC BY 4.0) for locations
 * outside KNMI/DWD radar coverage. Model precipitation at 15-minute
 * resolution — lower fidelity than radar, so the UI renders 15-minute bars
 * for it instead of 5-minute ones.
 *
 * The name MUST match `Source.OPEN_METEO.displayName` exactly: call sites
 * map a returned providerName back to its Source entry via the registry.
 */
const val PROVIDER_OPEN_METEO = "Open Meteo"

/** DWD RV value scale: raw grid units are 0.01 mm per 5 minutes. */
const val DWD_MM_PER_UNIT = 0.01

/**
 * Decompresses one DWD radar frame (base64 + zlib int16 little-endian grid)
 * and returns the unsigned value at grid position ([ix], [iy]).
 */
fun extractDwdGridValue(base64Zlib: String, width: Int, ix: Int, iy: Int): Double? {
    return try {
        val compressed = Base64.decode(base64Zlib, Base64.NO_WRAP)
        val inflated = inflateZlib(compressed) ?: return null

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

private fun inflateZlib(data: ByteArray): ByteArray? {
    return try {
        val inflater = Inflater()
        inflater.setInput(data)
        val output = ByteArrayOutputStream(data.size * 4)
        val buffer = ByteArray(8192)
        // Cap the inflated output: the data comes off the network and a sane
        // crop grid is at most a few hundred KB — refuse decompression bombs.
        val maxOutputBytes = 4_000_000
        while (!inflater.finished()) {
            val n = inflater.inflate(buffer)
            if (n == 0 && inflater.needsInput()) break
            output.write(buffer, 0, n)
            if (output.size() > maxOutputBytes) break
        }
        inflater.end()
        if (output.size() > maxOutputBytes) null else output.toByteArray()
    } catch (e: Exception) {
        null
    }
}

/** Bright Sky's radar endpoint wants a truncated-to-minute UTC timestamp. */
fun formatBrightSkyDate(instant: Instant): String {
    return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:00xxx")
        .withZone(ZoneOffset.UTC)
        .format(instant)
}

/** The KNMI WMS layer only has exact timestamps in this UTC format. */
fun formatUtcWms(instant: Instant): String {
    return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        .withZone(ZoneOffset.UTC)
        .format(instant)
}