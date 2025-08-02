package com.pranshulgg.weather_master_app

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.updateAll
import androidx.glance.layout.Column
import androidx.glance.layout.Text
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.state.GlanceStateDefinition
import org.json.JSONObject
import java.io.File

class WeatherWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    @Composable
    override fun Content(context: Context, id: GlanceId) {
        WeatherWidgetContent(context)
    }
}

@Composable
fun WeatherWidgetContent(context: Context) {
    val temp = try {
        val file = File(context.filesDir, "weather_data.json")
        val json = file.readText()
        val jsonObj = JSONObject(json)
        jsonObj.getDouble("temp").toString()
    } catch (e: Exception) {
        "N/A"
    }

    Column(modifier = GlanceModifier) {
        Text("Current Temp:")
        Text("$temp°C")
    }
}
