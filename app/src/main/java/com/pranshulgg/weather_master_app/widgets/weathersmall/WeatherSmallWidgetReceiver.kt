package com.pranshulgg.weather_master_app.widgets.weathersmall

import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.pranshulgg.weather_master_app.widgets.weather.WeatherWidget


class WeatherSmallWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = WeatherSmallWidget()
}