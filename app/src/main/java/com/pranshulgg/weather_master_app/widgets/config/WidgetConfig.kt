package com.pranshulgg.weather_master_app.widgets.config

import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetTextTheme
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetTheme
import kotlinx.serialization.Serializable

@Serializable
data class WidgetConfig(
    val clockSize: Float = 32f,
    val showClock: Boolean = true,
    val dateFormat: String = "EEE d MMM",
    val widgetTextTheme: WidgetTextTheme = WidgetTextTheme.AUTO,
    val widgetTheme: WidgetTheme = WidgetTheme.AUTO

)