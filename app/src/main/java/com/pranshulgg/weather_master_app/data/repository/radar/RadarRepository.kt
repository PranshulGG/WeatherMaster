package com.pranshulgg.weather_master_app.data.repository.radar

import android.graphics.Bitmap
import androidx.annotation.StringRes
import com.pranshulgg.weather_master_app.core.model.domain.radar.RadarFrame

/** A radar source serving Web Mercator tiles (the slippy-map scheme) */
interface RadarRepository {

    /** Tile width and height in pixels */
    val tileSizePx: Int

    /** Highest zoom level the source serves */
    val maxZoom: Int

    /** Credit shown on the map, as the source's licence asks */
    @get:StringRes
    val attribution: Int

    /** Recent radar frames, oldest first (the last one is the latest) */
    suspend fun getFrames(): List<RadarFrame>

    /** One tile of [frame], transparent where there is no precipitation. Null if it can't be loaded */
    suspend fun getTile(frame: RadarFrame, zoom: Int, x: Int, y: Int): Bitmap?
}
