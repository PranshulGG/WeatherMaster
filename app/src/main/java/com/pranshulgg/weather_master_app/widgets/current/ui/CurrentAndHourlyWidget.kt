package com.pranshulgg.weather_master_app.widgets.current.ui

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import com.pranshulgg.weather_master_app.widgets.WidgetConfig
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import kotlinx.serialization.json.Json

class CurrentAndHourlyWidget : GlanceAppWidget() {


    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {

        provideContent {

            val prefs = currentState<Preferences>()
            val json = prefs[WidgetConfig.WEATHER_STATE_KEY]

            val state = json?.let {
                Json.Default.decodeFromString<WidgetWeather>(it)
            }


            CurrentAndHourlyWidgetContent(state, context)
        }
    }
}