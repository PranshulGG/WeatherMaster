package com.pranshulgg.weather_master_app.feature.radar

import android.content.Context
import com.pranshulgg.weather_master_app.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.lang.ref.SoftReference
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * One outline layer as polylines, in Web Mercator coordinates (x 0..1 west to east, y 0..1 north
 * to south), projected once when loaded. Points of polyline i are [starts][i] until
 * [starts][i + 1]; [bounds] holds minX, minY, maxX, maxY per polyline
 */
class OutlineLayer(
    val starts: IntArray,
    val xs: FloatArray,
    val ys: FloatArray,
    val bounds: FloatArray
) {
    val polylineCount: Int get() = starts.size - 1
}

/**
 * World map outlines drawn with the radar, from Natural Earth 1:10m (public domain): coastlines,
 * country borders, state/province borders, rivers and lakes (major = its worldwide datasets,
 * minor = its North America and Europe supplements) and roads (major highways and expressways,
 * secondary highways). Water and roads are split so less is drawn when zoomed out
 *
 * File format (big-endian), per polyline, coordinates in units of 1e-5 degree:
 * int32 point count, int32 lat and int32 lon of the first point, then int16 dLat, int16 dLon steps
 */
class RadarOutlines(
    val coast: OutlineLayer,
    val countries: OutlineLayer,
    val provinces: OutlineLayer,
    val water: OutlineLayer,
    val minorWater: OutlineLayer,
    val majorRoads: OutlineLayer,
    val secondaryRoads: OutlineLayer
) {

    companion object {
        private val mutex = Mutex()

        // About 16 MB once loaded; soft so it can be reclaimed while the radar isn't used
        private var loaded: SoftReference<RadarOutlines>? = null

        suspend fun load(context: Context): RadarOutlines = mutex.withLock {
            loaded?.get() ?: withContext(Dispatchers.IO) {
                RadarOutlines(
                    coast = readLayer(context, R.raw.radar_world_coast),
                    countries = readLayer(context, R.raw.radar_world_countries),
                    provinces = readLayer(context, R.raw.radar_world_provinces),
                    water = readLayer(context, R.raw.radar_world_water),
                    minorWater = readLayer(context, R.raw.radar_world_water_minor),
                    majorRoads = readLayer(context, R.raw.radar_world_roads_major),
                    secondaryRoads = readLayer(context, R.raw.radar_world_roads_secondary)
                )
            }.also { loaded = SoftReference(it) }
        }

        private fun readLayer(context: Context, resourceId: Int): OutlineLayer {
            val bytes = context.resources.openRawResource(resourceId).use { it.readBytes() }
            val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)

            // First pass: count polylines and points to size the arrays
            var polylines = 0
            var points = 0
            while (buffer.remaining() >= 12) {
                val count = buffer.int
                buffer.position(buffer.position() + 8 + (count - 1) * 4)
                polylines++
                points += count
            }
            buffer.rewind()

            val starts = IntArray(polylines + 1)
            val xs = FloatArray(points)
            val ys = FloatArray(points)
            val bounds = FloatArray(polylines * 4)
            var point = 0

            for (line in 0 until polylines) {
                val count = buffer.int
                var lat = buffer.int
                var lon = buffer.int
                starts[line] = point
                repeat(count) { i ->
                    if (i > 0) {
                        lat += buffer.short
                        lon += buffer.short
                    }
                    xs[point] = mercatorX(lon / 1e5).toFloat()
                    ys[point] = mercatorY(lat / 1e5).toFloat()
                    point++
                }
                val first = starts[line]
                var minX = xs[first]
                var maxX = minX
                var minY = ys[first]
                var maxY = minY
                for (p in first + 1 until point) {
                    if (xs[p] < minX) minX = xs[p] else if (xs[p] > maxX) maxX = xs[p]
                    if (ys[p] < minY) minY = ys[p] else if (ys[p] > maxY) maxY = ys[p]
                }
                bounds[line * 4] = minX
                bounds[line * 4 + 1] = minY
                bounds[line * 4 + 2] = maxX
                bounds[line * 4 + 3] = maxY
            }
            starts[polylines] = point

            return OutlineLayer(starts, xs, ys, bounds)
        }
    }
}
