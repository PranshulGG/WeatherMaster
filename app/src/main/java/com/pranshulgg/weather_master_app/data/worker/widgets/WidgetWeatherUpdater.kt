package com.pranshulgg.weather_master_app.data.worker.widgets

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.pranshulgg.weather_master_app.core.prefs.helper.PreferencesHelper
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateDefinition
import com.pranshulgg.weather_master_app.widgets.config.WidgetConfig
import com.pranshulgg.weather_master_app.widgets.glance.GlanceWidget
import com.pranshulgg.weather_master_app.widgets.pill.WidgetPill
import com.pranshulgg.weather_master_app.widgets.summary.SummaryWidget
import com.pranshulgg.weather_master_app.widgets.weather.WeatherWidget
import com.pranshulgg.weather_master_app.widgets.weatherclockdaily.ClockDailyWidget
import com.pranshulgg.weather_master_app.widgets.weatherhorizontal.WeatherHorizontalWidget

class WeatherWidgetUpdater(
    private val context: Context
) {

    private val widget = WeatherWidget()
    private val pill = WidgetPill()
    private val summary = SummaryWidget()
    private val widgetHorizontal = WeatherHorizontalWidget()
    private val widgetGlance = GlanceWidget()

    private val widgetClockDaily = ClockDailyWidget()

    suspend fun update(json: String) {
        val manager = GlanceAppWidgetManager(context)

        suspend fun <T : GlanceAppWidget> updateWidgets(
            widget: T,
            ids: List<GlanceId>
        ) {
            ids.forEach { id ->
                try {
                    updateAppWidgetState(context, WeatherWidgetStateDefinition, id) { current ->
                        current.copy(json = json, config = current.config)
                    }
                    widget.update(context, id)
                } catch (e: Exception) {
                    Log.e("WeatherWidgetUpdater", "Failed to update widget $id", e)
                }
            }
        }

        updateWidgets(widget, manager.getGlanceIds(WeatherWidget::class.java))
        updateWidgets(pill, manager.getGlanceIds(WidgetPill::class.java))
        updateWidgets(summary, manager.getGlanceIds(SummaryWidget::class.java))
        updateWidgets(widgetHorizontal, manager.getGlanceIds(WeatherHorizontalWidget::class.java))
        updateWidgets(widgetGlance, manager.getGlanceIds(GlanceWidget::class.java))
        updateWidgets(widgetClockDaily, manager.getGlanceIds(ClockDailyWidget::class.java))
    }

    suspend fun saveWidgetConfig(
        context: Context,
        widgetId: Int,
        config: WidgetConfig
    ) {

        val manager =
            GlanceAppWidgetManager(context)

        val glanceId =
            manager.getGlanceIdBy(widgetId)

        updateAppWidgetState(
            context,
            WeatherWidgetStateDefinition,
            glanceId
        ) {
            it.copy(
                config = config
            )
        }
    }
}