package com.pranshulgg.weather_master_app

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.pranshulgg.weather_master_app.util.WeatherIconMapper


class WeatherWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        for (id in ids) {
            val views = RemoteViews(context.packageName, R.layout.weather_widget)
            val prefs = context.getSharedPreferences("HomeWidgetPreferences", Context.MODE_PRIVATE)
            val temp = prefs.getString("temperatureCurrentPill", "--")
            val code = prefs.getString("weather_codeCurrentPill", "--")
            

            // Prevents crash when isDayWidget was stored as Boolean instead of String
            val rawIsDay = prefs.all["isDayWidget"]
            val isDay = when (rawIsDay) {
                is String -> rawIsDay
                is Boolean -> if (rawIsDay) "1" else "0"
                else -> "1" 
            }

            val max = prefs.getString("todayMax", "--")
            val min = prefs.getString("todayMin", "--")

            views.setTextViewText(R.id.current_widget_temp_pill, "$temp°")
            views.setTextViewText(R.id.temp_high, "$max° •")
            views.setTextViewText(R.id.temp_low, " $min°")

            val iconRes = WeatherIconMapper.getIconResource(code, isDay)
            views.setImageViewResource(R.id.widget_icon_current_pill, iconRes)

            views.setImageViewResource(R.id.widget_icon_current_pill, iconRes)

            manager.updateAppWidget(id, views)
        }
    }
}
