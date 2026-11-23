package com.pranshulgg.weather_master_app.feature.radar

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.log2
import kotlin.math.tan

class RadarColors(
    val coast: Int,
    val countries: Int,
    val provinces: Int,
    val water: Int,
    val roads: Int,
    val location: Int,
    val locationRing: Int
)

private const val EARTH_CIRCUMFERENCE_M = 40_075_016.686
private const val MAX_MERCATOR_LAT = 85.05112878

// Level of detail: view widths (km) above which a layer isn't drawn
private const val DETAIL_MINOR_WATER_KM = 300.0
private const val DETAIL_SECONDARY_ROADS_KM = 400.0
private const val DETAIL_WATER_KM = 800.0
private const val DETAIL_MAJOR_ROADS_KM = 1500.0
private const val DETAIL_PROVINCES_KM = 2500.0

/** Web Mercator x (0..1 west to east) and y (0..1 north to south) of a longitude / latitude */
internal fun mercatorX(lon: Double) = (lon + 180.0) / 360.0

internal fun mercatorY(lat: Double): Double {
    val r = Math.toRadians(lat.coerceIn(-MAX_MERCATOR_LAT, MAX_MERCATOR_LAT))
    return (1.0 - ln(tan(r) + 1.0 / cos(r)) / PI) / 2.0
}

/**
 * A Web Mercator view (the projection radar tiles use) centred on a location, [widthKm] across
 * [widthPx] at the centre's latitude
 */
class MercatorView(
    centerLatitude: Double,
    centerLongitude: Double,
    val widthKm: Double,
    val widthPx: Int,
    val heightPx: Int
) {
    // Mercator stops at about 85°: a location nearer the pole is shown at that latitude
    private val latitude = centerLatitude.coerceIn(-MAX_MERCATOR_LAT, MAX_MERCATOR_LAT)

    /** Width of the whole world in pixels at this scale */
    val worldPx: Double = widthPx * EARTH_CIRCUMFERENCE_M * cos(Math.toRadians(latitude)) / (widthKm * 1000)

    val left: Double = mercatorX(centerLongitude) * worldPx - widthPx / 2.0
    val top: Double = mercatorY(latitude) * worldPx - heightPx / 2.0

    /** Visible area in Mercator coordinates: minX, minY, maxX, maxY */
    val bounds = floatArrayOf(
        (left / worldPx).toFloat(),
        (top / worldPx).toFloat(),
        ((left + widthPx) / worldPx).toFloat(),
        ((top + heightPx) / worldPx).toFloat()
    )

    /** Screen position of Mercator coordinates */
    fun x(mercatorX: Float): Float = (mercatorX * worldPx - left).toFloat()
    fun y(mercatorY: Float): Float = (mercatorY * worldPx - top).toFloat()
}

/** Radar tiles drawn for a view; [missingTiles] couldn't be loaded (e.g. offline), so retry later */
class RenderedEchoes(val bitmap: Bitmap, val missingTiles: Int)

/** Draws radar tiles and the map (outlines and the location marker) to separate bitmaps */
object RadarRenderer {

    /**
     * Radar tiles of one frame on a transparent bitmap. [scale] below 1 draws at lower resolution
     * (for loop frames), using tiles one zoom level coarser; the bitmap is shown enlarged by 1 / [scale]
     */
    suspend fun renderEchoes(
        view: MercatorView,
        tileSizePx: Int,
        maxZoom: Int,
        scale: Float = 1f,
        loadTile: suspend (zoom: Int, x: Int, y: Int) -> Bitmap?
    ): RenderedEchoes {
        // The zoom where a tile pixel covers one to two screen pixels: plenty for radar data
        // (about 1 km at best)
        val coarser = if (scale < 1f) 1 else 0
        val zoom = (floor(log2(view.worldPx / tileSizePx)).toInt() - coarser).coerceIn(0, maxZoom)
        val tileCount = 1 shl zoom
        val tilePx = view.worldPx / tileCount // size of one tile on screen, in full-size pixels
        val firstX = floor(view.left / tilePx).toInt()
        val lastX = floor((view.left + view.widthPx) / tilePx).toInt()
        val firstY = floor(view.top / tilePx).toInt().coerceAtLeast(0)
        val lastY = floor((view.top + view.heightPx) / tilePx).toInt().coerceAtMost(tileCount - 1)

        // Screen columns, a little over two worlds wide at most (more is never visible at the
        // widest zoom; the world repeats, so columns share tiles)
        val columns = (firstX..lastX).take(tileCount * 2 + 2)
        // Each distinct tile is loaded once, even if it appears in more than one column
        val needed = (firstY..lastY).flatMap { y -> columns.map { x -> Math.floorMod(x, tileCount) to y } }.distinct()
        val tiles = coroutineScope {
            needed.map { (x, y) -> async { (x to y) to loadTile(zoom, x, y) } }.awaitAll().toMap()
        }

        // Allocated only once the tiles are in, so a render waiting on the network holds no bitmap
        val width = (view.widthPx * scale).toInt().coerceAtLeast(1)
        val height = (view.heightPx * scale).toInt().coerceAtLeast(1)
        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.FILTER_BITMAP_FLAG)
        val rect = RectF()
        for (y in firstY..lastY) {
            for (x in columns) {
                val tile = tiles[Math.floorMod(x, tileCount) to y] ?: continue
                val left = ((x * tilePx - view.left) * scale).toFloat()
                val top = ((y * tilePx - view.top) * scale).toFloat()
                val size = (tilePx * scale).toFloat()
                rect.set(left, top, left + size, top + size)
                canvas.drawBitmap(tile, null, rect, paint)
            }
        }
        return RenderedEchoes(bitmap, missingTiles = tiles.values.count { it == null })
    }

    /** Map outlines and the location marker on a transparent bitmap, drawn over the radar */
    suspend fun renderMap(
        outlines: RadarOutlines,
        view: MercatorView,
        density: Float,
        colors: RadarColors
    ): Bitmap {
        val widthKm = view.widthKm
        val bitmap = createBitmap(view.widthPx, view.heightPx)
        val canvas = Canvas(bitmap)
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
        }

        val layers = buildList {
            if (widthKm <= DETAIL_MINOR_WATER_KM) add(Triple(outlines.minorWater, colors.water, 0.8f))
            if (widthKm <= DETAIL_WATER_KM) add(Triple(outlines.water, colors.water, 0.8f))
            if (widthKm <= DETAIL_SECONDARY_ROADS_KM) add(Triple(outlines.secondaryRoads, colors.roads, 0.8f))
            if (widthKm <= DETAIL_MAJOR_ROADS_KM) add(Triple(outlines.majorRoads, colors.roads, 0.8f))
            if (widthKm <= DETAIL_PROVINCES_KM) add(Triple(outlines.provinces, colors.provinces, 1f))
            add(Triple(outlines.coast, colors.coast, 1.2f))
            add(Triple(outlines.countries, colors.countries, 2f))
        }
        for ((layer, color, widthDp) in layers) {
            currentCoroutineContext().ensureActive() // stop early if a newer render replaced this one
            linePaint.color = color
            linePaint.strokeWidth = widthDp * density
            drawLayer(canvas, layer, view, linePaint)
        }
        drawLocation(canvas, view.widthPx / 2f, view.heightPx / 2f, density, colors)

        return bitmap
    }

    private fun drawLayer(canvas: Canvas, layer: OutlineLayer, view: MercatorView, paint: Paint) {
        val path = Path()
        val bounds = layer.bounds
        val visible = view.bounds
        for (line in 0 until layer.polylineCount) {
            val b = line * 4
            if (bounds[b + 2] < visible[0] || bounds[b] > visible[2] ||
                bounds[b + 3] < visible[1] || bounds[b + 1] > visible[3]
            ) continue
            val first = layer.starts[line]
            val last = layer.starts[line + 1] - 1
            var lastX = view.x(layer.xs[first])
            var lastY = view.y(layer.ys[first])
            path.moveTo(lastX, lastY)
            for (point in first + 1..last) {
                val x = view.x(layer.xs[point])
                val y = view.y(layer.ys[point])
                // Skip points within a pixel of the last one drawn (most of them when zoomed out)
                if (point != last && abs(x - lastX) < 1f && abs(y - lastY) < 1f) continue
                path.lineTo(x, y)
                lastX = x
                lastY = y
            }
        }
        canvas.drawPath(path, paint)
    }

    private fun drawLocation(
        canvas: Canvas,
        x: Float,
        y: Float,
        density: Float,
        colors: RadarColors
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = colors.locationRing
        canvas.drawCircle(x, y, 7f * density, paint)
        paint.color = colors.location
        canvas.drawCircle(x, y, 5f * density, paint)
    }
}
