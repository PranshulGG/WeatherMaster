package com.pranshulgg.weather_master_app.widgets.froggy

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.pranshulgg.weather_master_app.data.worker.WeatherWorker
import com.pranshulgg.weather_master_app.widgets.current.ui.CurrentAndHourlyWidget
import com.pranshulgg.weather_master_app.widgets.froggy.ui.FroggyWidget


class FroggyWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = FroggyWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)

        WeatherWorker.startNow(context)
    }
}