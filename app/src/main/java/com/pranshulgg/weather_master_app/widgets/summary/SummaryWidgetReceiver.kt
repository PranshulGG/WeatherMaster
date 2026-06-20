package com.pranshulgg.weather_master_app.widgets.summary

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.pranshulgg.weather_master_app.data.worker.WeatherWorker


class SummaryWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = SummaryWidget()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        WeatherWorker.runWorkerOnce(context)
    }
}