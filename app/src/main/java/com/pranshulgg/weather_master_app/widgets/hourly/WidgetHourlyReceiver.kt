package com.pranshulgg.weather_master_app.widgets.hourly

import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.pranshulgg.weather_master_app.widgets.pill.WidgetPill


class WidgetHourlyReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = WidgetHourly()

}