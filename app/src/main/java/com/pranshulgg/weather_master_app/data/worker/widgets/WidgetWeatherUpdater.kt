package com.pranshulgg.weather_master_app.data.worker.widgets

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.pranshulgg.weather_master_app.widgets.WidgetConfig.WEATHER_STATE_KEY
import com.pranshulgg.weather_master_app.widgets.current.ui.CurrentAndHourlyWidget
import com.pranshulgg.weather_master_app.widgets.froggy.ui.FroggyWidget


class WeatherWidgetUpdater(
    private val context: Context
) {

    suspend fun update(json: String) {

        val manager = GlanceAppWidgetManager(context)


        val weatherIds =
            manager.getGlanceIds(CurrentAndHourlyWidget::class.java)

        val froggyIds =
            manager.getGlanceIds(FroggyWidget::class.java)
        val allIds = weatherIds + froggyIds


        /**
         * Not observing the DB, cuz we want, so just save the data into prefs
         * Widgets can later encode to WidgetWeather
         */
        allIds.forEach { id ->
            updateAppWidgetState(context, id) { prefs ->
                prefs[WEATHER_STATE_KEY] = json
            }
        }


        weatherIds.forEach { id ->

            CurrentAndHourlyWidget().update(context, id)
        }

        froggyIds.forEach { id ->

            FroggyWidget().update(context, id)
        }
    }
}