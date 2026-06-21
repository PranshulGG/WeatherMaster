package com.pranshulgg.weather_master_app.data.worker.widgets

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.pranshulgg.weather_master_app.core.prefs.helper.PreferencesHelper
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateDefinition
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateJson
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

        val manager =
            GlanceAppWidgetManager(context)

        val widgetIds = manager.getGlanceIds(WeatherWidget::class.java)
        val pillIds = manager.getGlanceIds(WidgetPill::class.java)
        val summaryIds = manager.getGlanceIds(SummaryWidget::class.java)
        val widgetHorizontalIds = manager.getGlanceIds(WeatherHorizontalWidget::class.java)
        val clockDailyIds = manager.getGlanceIds(ClockDailyWidget::class.java)

        val appTheme = PreferencesHelper.getString("app_theme") ?: "System"

        val widgetGlanceIds = manager.getGlanceIds(GlanceWidget::class.java)

        widgetIds.forEach {
            updateAppWidgetState(context, WeatherWidgetStateDefinition, it) { current ->
                current.copy(json = json, config = current.config, appTheme = appTheme)
            }
            widget.update(context, it)
        }

        pillIds.forEach {
            updateAppWidgetState(context, WeatherWidgetStateDefinition, it) { current ->
                current.copy(json = json, config = current.config, appTheme = appTheme)
            }
            pill.update(context, it)
        }

        summaryIds.forEach {
            updateAppWidgetState(context, WeatherWidgetStateDefinition, it) { current ->
                current.copy(json = json, config = current.config, appTheme = appTheme)
            }
            summary.update(context, it)
        }


        widgetHorizontalIds.forEach {
            updateAppWidgetState(context, WeatherWidgetStateDefinition, it) { current ->
                current.copy(json = json, config = current.config, appTheme = appTheme)
            }
            widgetHorizontal.update(context, it)
        }

        widgetGlanceIds.forEach {
            updateAppWidgetState(context, WeatherWidgetStateDefinition, it) { current ->
                current.copy(json = json, config = current.config, appTheme = appTheme)
            }
            widgetGlance.update(context, it)
        }
        clockDailyIds.forEach {
            updateAppWidgetState(context, WeatherWidgetStateDefinition, it) { current ->
                current.copy(json = json, config = current.config, appTheme = appTheme)
            }
            widgetClockDaily.update(context, it)
        }
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