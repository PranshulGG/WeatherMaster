package com.pranshulgg.weather_master_app.widgets.config

import com.pranshulgg.weather_master_app.widgets.model.WidgetVariant
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetTextTheme
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetTheme
import kotlinx.serialization.Serializable

@Serializable
data class WidgetConfig(
    val clockSize: Float = 32f,
    val showClock: Boolean = true,
    val dateFormat: String = "EEE d MMM",
    val widgetTextTheme: WidgetTextTheme = WidgetTextTheme.AUTO,
    val widgetTheme: WidgetTheme = WidgetTheme.AUTO,
    val hourlyCount: Int = 6,
    val variant: WidgetVariant = WidgetVariant.LARGE,
    val fontSize: Float = 1f,
    val iconSize: Float = 1f,
    val dailyCount: Int = 4

)