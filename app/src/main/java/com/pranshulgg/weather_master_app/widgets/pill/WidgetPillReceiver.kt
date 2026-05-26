package com.pranshulgg.weather_master_app.widgets.pill

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.pranshulgg.weather_master_app.data.worker.WeatherWorker
import com.pranshulgg.weather_master_app.widgets.weather.WeatherWidget


class WidgetPillReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = WidgetPill()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)

        WeatherWorker.startNow(context)
    }
}