package com.pranshulgg.weather_master_app.widgets.params

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlin.math.abs

private fun Dp.approx(other: Dp, tolerance: Dp = 8.dp): Boolean {
    return (value - other.value).let { abs(it) } <= tolerance.value
}

fun getWidgetParams(size: DpSize): WidgetParams {

    val width = size.width
    val height = size.height

    val isHorizontal =
        height.approx(40.dp) && width > 60.dp

    val sizeHorizontal = when {
        width.approx(140.dp) && height.approx(40.dp) ->
            WidgetSize.TINY

        width.approx(220.dp) && height.approx(40.dp) ->
            WidgetSize.SMALL

        width.approx(301.dp) && height.approx(40.dp) ->
            WidgetSize.MEDIUM

        width.approx(382.dp) && height.approx(40.dp) ->
            WidgetSize.LARGE

        else -> WidgetSize.LARGE
    }

    val sizeVertical = when {
        width.approx(60.dp) && height.approx(40.dp) ->
            WidgetSize.TINY

        width.approx(140.dp) && height.approx(208.dp) ->
            WidgetSize.SMALL

        width.approx(220.dp) && height.approx(208.dp) ->
            WidgetSize.MEDIUM

        width.approx(301.dp) && height.approx(208.dp) ->
            WidgetSize.LARGE

        else -> WidgetSize.LARGE
    }

    return WidgetParams(
        type = if (isHorizontal) {
            WidgetSizeType.HORIZONTAL
        } else {
            WidgetSizeType.VERTICAL
        },
        size = if (isHorizontal) {
            sizeHorizontal
        } else {
            sizeVertical
        }
    )
}
