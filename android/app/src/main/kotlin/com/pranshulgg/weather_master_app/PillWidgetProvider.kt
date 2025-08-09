package com.pranshulgg.weather_master_app

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.pranshulgg.weather_master_app.util.WeatherIconMapper


class PillWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        for (id in ids) {
            val views = RemoteViews(context.packageName, R.layout.pill_widget)
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



            views.setTextViewText(R.id.current_widget_temp_pill_pixel, "$temp°")


            val iconRes = WeatherIconMapper.getIconResource(code, isDay)
            views.setImageViewResource(R.id.widget_icon_current_pill_pixel, iconRes)

            views.setImageViewResource(R.id.widget_icon_current_pill_pixel, iconRes)

            manager.updateAppWidget(id, views)
        }
    }
}
