package com.pranshulgg.weather_master_app.widgets

import com.pranshulgg.weather_master_app.widgets.config.WidgetConfig
import kotlinx.serialization.Serializable

@Serializable
data class WeatherWidgetStateJson(
    val json: String? = null,
    val config: WidgetConfig = WidgetConfig()
)