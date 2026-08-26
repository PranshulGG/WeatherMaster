package com.pranshulgg.weather_master_app.widgets.weather5

import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.pranshulgg.weather_master_app.widgets.weather4.Weather4Widget


class Weather5WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = Weather5Widget()
}