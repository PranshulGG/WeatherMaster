package com.pranshulgg.weather_master_app.data.worker.widgets

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateDefinition
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateJson
import com.pranshulgg.weather_master_app.widgets.pill.WidgetPill
import com.pranshulgg.weather_master_app.widgets.summary.SummaryWidget
import com.pranshulgg.weather_master_app.widgets.weather.WeatherWidget
import com.pranshulgg.weather_master_app.widgets.weatherhorizontal.WeatherHorizontalWidget
import com.pranshulgg.weather_master_app.widgets.weatherhorizontal.ui.WeatherWidgetHorizontal
import com.pranshulgg.weather_master_app.widgets.weathersmall.WeatherSmallWidget
import com.pranshulgg.weather_master_app.widgets.weathersmall.ui.WeatherWidgetSmall

class WeatherWidgetUpdater(
    private val context: Context
) {

    private val widget = WeatherWidget()
    private val pill = WidgetPill()
    private val summary = SummaryWidget()
    private val widgetSmall = WeatherSmallWidget()

    private val widgetHorizontal = WeatherHorizontalWidget()

    suspend fun update(json: String) {

        val manager =
            GlanceAppWidgetManager(context)

        val widgetIds = manager.getGlanceIds(WeatherWidget::class.java)
        val pillIds = manager.getGlanceIds(WidgetPill::class.java)
        val summaryIds = manager.getGlanceIds(SummaryWidget::class.java)
        val widgetSmallIds = manager.getGlanceIds(WeatherSmallWidget::class.java)
        val widgetHorizontalIds = manager.getGlanceIds(WeatherHorizontalWidget::class.java)

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

        summaryIds.forEach {
            updateAppWidgetState(context, WeatherWidgetStateDefinition, it) {
                WeatherWidgetStateJson(json)
            }
            summary.update(context, it)
        }

        widgetSmallIds.forEach {
            updateAppWidgetState(context, WeatherWidgetStateDefinition, it) {
                WeatherWidgetStateJson(json)
            }
            widgetSmall.update(context, it)
        }

        widgetHorizontalIds.forEach {
            updateAppWidgetState(context, WeatherWidgetStateDefinition, it) {
                WeatherWidgetStateJson(json)
            }
            widgetHorizontal.update(context, it)
        }
    }
}