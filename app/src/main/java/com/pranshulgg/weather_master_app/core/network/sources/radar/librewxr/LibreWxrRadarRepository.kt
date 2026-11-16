package com.pranshulgg.weather_master_app.core.network.sources.radar.librewxr

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.radar.RadarFrame
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.data.repository.radar.RadarRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibreWxrRadarRepository @Inject constructor(
    private val api: LibreWxrApi
) : RadarRepository {

    override val tileSizePx = 512
    override val maxZoom = 12 // the public server refuses higher levels
    override val attribution = R.string.radar_credit

    // Recently used tiles as compressed PNG bytes (mostly a few KB; a decoded tile is 1 MB), so
    // zooming back, the time lapse and redraws don't download them again
    private val tiles = object : LruCache<String, ByteArray>(TILE_CACHE_BYTES) {
        override fun sizeOf(key: String, value: ByteArray) = value.size
    }

    // Be gentle with the free public server
    private val downloads = Semaphore(PARALLEL_DOWNLOADS)

    override suspend fun getFrames(): List<RadarFrame> {
        val maps = safeApiCall { api.fetchWeatherMaps() }.getOrThrow()
        return maps.radar?.past.orEmpty()
            .mapNotNull { frame ->
                val time = frame.time ?: return@mapNotNull null
                // Only a path like /v2/radar/1794757800 is used to build tile URLs
                val path = frame.path?.takeIf { it.matches(FRAME_PATH) } ?: return@mapNotNull null
                RadarFrame(timeMilli = time * 1000, path = path.trimStart('/'))
            }
            .sortedBy { it.timeMilli }
            .ifEmpty { throw AppException.Server() }
    }

    override suspend fun getTile(frame: RadarFrame, zoom: Int, x: Int, y: Int): Bitmap? {
        val key = "${frame.path}/$zoom/$x/$y"
        val bytes = tiles.get(key) ?: download(frame, zoom, x, y)?.also { tiles.put(key, it) } ?: return null
        return withContext(Dispatchers.Default) {
            // Only a tile of the expected size is decoded (a bad image can't claim a huge size)
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
            if (bounds.outWidth != tileSizePx || bounds.outHeight != tileSizePx) return@withContext null
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
    }

    private suspend fun download(frame: RadarFrame, zoom: Int, x: Int, y: Int): ByteArray? =
        // The permit is held until the body is read, so at most 4 tiles stream at a time
        downloads.withPermit {
            val body = safeApiCall {
                api.fetchTile(frame.path, tileSizePx, zoom, x, y, COLOR_SCHEME, TILE_OPTIONS)
            }.getOrNull() ?: return@withPermit null
            // The body is streamed, so a dropped connection or timeout shows up while reading it
            try {
                body.use { response ->
                    withContext(Dispatchers.IO) {
                        // Tiles are small; refuse anything far larger
                        val input = response.byteStream()
                        val out = ByteArrayOutputStream()
                        val buffer = ByteArray(8192)
                        while (out.size() <= MAX_TILE_BYTES) {
                            val read = input.read(buffer, 0, minOf(buffer.size, MAX_TILE_BYTES + 1 - out.size()))
                            if (read < 0) break
                            out.write(buffer, 0, read)
                        }
                        out.takeIf { it.size() <= MAX_TILE_BYTES }?.toByteArray()
                    }
                }
            } catch (e: IOException) {
                null
            }
        }

    private companion object {
        val FRAME_PATH = Regex("/?v2/radar/\\d+")
        const val COLOR_SCHEME = 6 // "NEXRAD Level III"
        const val TILE_OPTIONS = "1_0" // smoothed, no separate snow colours
        const val PARALLEL_DOWNLOADS = 4
        const val MAX_TILE_BYTES = 1024 * 1024
        const val TILE_CACHE_BYTES = 16 * 1024 * 1024
    }
}
