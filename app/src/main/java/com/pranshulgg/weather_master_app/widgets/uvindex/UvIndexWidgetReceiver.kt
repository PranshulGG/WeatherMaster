package com.pranshulgg.weather_master_app.widgets.uvindex

import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.pranshulgg.weather_master_app.widgets.summary.SummaryWidget


class UvIndexWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = UvIndexWidget()

}