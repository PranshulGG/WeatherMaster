package com.pranshulgg.weather_master_app.widgets.glance

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.pranshulgg.weather_master_app.data.worker.WeatherWorker
import com.pranshulgg.weather_master_app.widgets.weather.WeatherWidget


class GlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = GlanceWidget()

}