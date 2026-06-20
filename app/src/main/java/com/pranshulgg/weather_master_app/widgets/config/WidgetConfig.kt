package com.pranshulgg.weather_master_app.widgets.config

import kotlinx.serialization.Serializable

@Serializable
data class WidgetConfig(
    val isTransparent: Boolean = false,
    val clockSize: Float = 32f,
    val showClock: Boolean = true,
    val dateFormat: String = "EEE d MMM"

)