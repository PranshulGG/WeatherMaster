package com.pranshulgg.weather_master_app.widgets.params

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlin.math.abs


fun getWidgetParams(size: DpSize): WidgetParams {

    val width = size.width
    val height = size.height

    val isHorizontal = height < 100.dp && width > 80.dp

    val sizeHorizontal = when {
        width < 160.dp -> WidgetSize.TINY
        width < 250.dp -> WidgetSize.SMALL
        width < 340.dp -> WidgetSize.MEDIUM
        else -> WidgetSize.LARGE
    }

    val sizeVertical = when {
        width < 80.dp -> WidgetSize.TINY
        width < 160.dp -> WidgetSize.SMALL
        width < 250.dp -> WidgetSize.MEDIUM
        else -> WidgetSize.LARGE
    }

    return WidgetParams(
        type = if (isHorizontal) WidgetSizeType.HORIZONTAL else WidgetSizeType.VERTICAL,
        size = if (isHorizontal) sizeHorizontal else sizeVertical
    )
}
