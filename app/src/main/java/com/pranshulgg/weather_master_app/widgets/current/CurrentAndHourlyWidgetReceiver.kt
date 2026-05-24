package com.pranshulgg.weather_master_app.widgets.current

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.pranshulgg.weather_master_app.data.worker.WeatherWorker
import com.pranshulgg.weather_master_app.widgets.current.ui.CurrentAndHourlyWidget

class CurrentAndHourlyWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = CurrentAndHourlyWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)

        WeatherWorker.startNow(context)
    }
}