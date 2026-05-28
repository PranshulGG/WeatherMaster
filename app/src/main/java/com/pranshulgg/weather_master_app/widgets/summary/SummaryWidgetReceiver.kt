package com.pranshulgg.weather_master_app.widgets.summary

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.pranshulgg.weather_master_app.data.worker.WeatherWorker


class SummaryWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = SummaryWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)

        WeatherWorker.startNow(context)
    }
}