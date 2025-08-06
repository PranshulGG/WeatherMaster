package com.pranshulgg.weather_master_app

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.pranshulgg.weather_master_app.util.WeatherIconMapper
class WeatherWidgetCastProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        for (id in ids) {
            val views = RemoteViews(context.packageName, R.layout.widget_hourly_current)
            val prefs = context.getSharedPreferences("HomeWidgetPreferences", Context.MODE_PRIVATE)
            val temp = prefs.getString("temperatureCurrentPill", "--")
            val code = prefs.getString("weather_codeCurrentPill", "--")
            val conditionName = prefs.getString("locationCurrentConditon", "--")
            val locationName = prefs.getString("locationNameWidget", "--")


            // Prevents crash when isDayWidget was stored as Boolean instead of String
            val rawIsDay = prefs.all["isDayWidget"]
            val isDay = when (rawIsDay) {
                is String -> rawIsDay
                is Boolean -> if (rawIsDay) "1" else "0"
                else -> "1"
            }

            val max = prefs.getString("todayMax", "--")
            val min = prefs.getString("todayMin", "--")

            views.setTextViewText(R.id.current_widget_temp_cast, "$temp°")
            views.setTextViewText(R.id.temp_high_cast, "$max° •")
            views.setTextViewText(R.id.temp_low_cast, " $min°")
            views.setTextViewText(R.id.weather_condition_current, "$conditionName")
            views.setTextViewText(R.id.current_widget_locationName, "$locationName")

            val iconRes = WeatherIconMapper.getIconResource(code, isDay)
            views.setImageViewResource(R.id.widget_icon_hourly_cast, iconRes)

            views.setImageViewResource(R.id.widget_icon_hourly_cast, iconRes)

            val containerId = R.id.hourly_cast
            views.removeAllViews(containerId)

            // Add 4 forecast items
            for (i in 0 until 4) {
                val hTemp = prefs.getString("hourly_temp_$i", "--")
                val hTime = prefs.getString("hourly_time_$i", "--") ?: "--"
                val hCode = prefs.getString("hourly_code_$i", "--")

                val itemView = RemoteViews(context.packageName, R.layout.hourly_item)
                itemView.setTextViewText(R.id.hourly_temp, "$hTemp°")
                itemView.setTextViewText(R.id.hourly_time, hTime)

                val hourlyIcon = WeatherIconMapper.getIconResource(hCode, isDay)
                itemView.setImageViewResource(R.id.hourly_icon, hourlyIcon)

                views.addView(containerId, itemView)
            }

            manager.updateAppWidget(id, views)
        }
    }
}
