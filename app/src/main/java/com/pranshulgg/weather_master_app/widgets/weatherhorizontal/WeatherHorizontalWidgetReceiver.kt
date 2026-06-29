package com.pranshulgg.weather_master_app.widgets.weatherhorizontal

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.pranshulgg.weather_master_app.data.worker.WeatherWorker


class WeatherHorizontalWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = WeatherHorizontalWidget()

}