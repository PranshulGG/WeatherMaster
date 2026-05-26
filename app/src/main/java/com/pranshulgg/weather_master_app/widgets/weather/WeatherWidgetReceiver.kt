package com.pranshulgg.weather_master_app.widgets.weather

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.pranshulgg.weather_master_app.data.worker.WeatherWorker

class WeatherWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = WeatherWidget()
    
    override fun onEnabled(context: Context) {
        super.onEnabled(context)

        WeatherWorker.startNow(context)
    }
}