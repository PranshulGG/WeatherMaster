package com.pranshulgg.weather_master_app.widgets.params


enum class WidgetSize {
    TINY,
    SMALL,
    MEDIUM,
    LARGE
}

enum class WidgetSizeType {
    HORIZONTAL,
    VERTICAL
}

data class WidgetParams(
    val type: WidgetSizeType,
    val size: WidgetSize
)
