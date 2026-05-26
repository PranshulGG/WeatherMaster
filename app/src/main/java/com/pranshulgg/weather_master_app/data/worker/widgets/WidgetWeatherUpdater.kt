package com.pranshulgg.weather_master_app.data.worker.widgets

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateDefinition
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateJson
import com.pranshulgg.weather_master_app.widgets.pill.WidgetPill
import com.pranshulgg.weather_master_app.widgets.weather.WeatherWidget

class WeatherWidgetUpdater(
    private val context: Context
) {

    private val widget = WeatherWidget()
    private val pill = WidgetPill()

    suspend fun update(json: String) {

        val manager =
            GlanceAppWidgetManager(context)

        val widgetIds = manager.getGlanceIds(WeatherWidget::class.java)
        val pillIds = manager.getGlanceIds(WidgetPill::class.java)

        widgetIds.forEach {
            updateAppWidgetState(context, WeatherWidgetStateDefinition, it) {
                WeatherWidgetStateJson(json)
            }
            widget.update(context, it)
        }

        pillIds.forEach {
            updateAppWidgetState(context, WeatherWidgetStateDefinition, it) {
                WeatherWidgetStateJson(json)
            }
            pill.update(context, it)
        }
    }
}