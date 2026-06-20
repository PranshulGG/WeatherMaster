package com.pranshulgg.weather_master_app.widgets.pill

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.pranshulgg.weather_master_app.data.worker.WeatherWorker
import com.pranshulgg.weather_master_app.widgets.weather.WeatherWidget


class WidgetPillReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = WidgetPill()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        WeatherWorker.runWorkerOnce(context)
    }
}