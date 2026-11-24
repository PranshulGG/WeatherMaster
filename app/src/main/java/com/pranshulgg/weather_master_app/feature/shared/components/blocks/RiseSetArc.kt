package com.pranshulgg.weather_master_app.feature.shared.components.blocks

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathMeasure

/**
 * Height of the rise/set times panel at the bottom of the sun and moon blocks; its top edge is the horizon
 */
internal const val RISE_SET_PANEL_HEIGHT_FRACTION = 0.4f

/**
 * Position of the sun/moon icon on the arc
 *
 * Rise and set map to where the arc crosses the horizon (top of the rise/set panel), not to the ends
 * of the arc which are below it, so the icon is above the horizon exactly while the sun/moon is up.
 * Before rise it waits at the start of the arc and after set at the end, both below the horizon
 */
internal fun riseSetArcPosition(
    pathMeasure: PathMeasure,
    horizonY: Float,
    now: Long,
    rise: Long,
    set: Long
): Offset {
    val length = pathMeasure.length
    val steps = 200

    val aboveHorizon = (0..steps)
        .map { length * it / steps }
        .filter { pathMeasure.getPosition(it).y <= horizonY }

    val riseDistance = aboveHorizon.firstOrNull() ?: 0f
    val setDistance = aboveHorizon.lastOrNull() ?: length

    val distance = when {
        now < rise -> 0f
        now > set -> length
        else -> riseDistance + (setDistance - riseDistance) *
                ((now - rise).toFloat() / (set - rise).coerceAtLeast(1))
    }

    return pathMeasure.getPosition(distance)
}
