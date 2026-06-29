package com.pranshulgg.weather_master_app.widgets.weatherclockdaily

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.pranshulgg.weather_master_app.data.worker.WeatherWorker


class ClockDailyWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = ClockDailyWidget()

}