package com.pranshulgg.weather_master_app

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import android.app.PendingIntent
import android.provider.AlarmClock
import android.provider.CalendarContract
import com.pranshulgg.weather_master_app.util.WeatherIconMapper

class clockDateWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        val prefs = context.getSharedPreferences("HomeWidgetPreferences", Context.MODE_PRIVATE)
        val showClock = prefs.getBoolean("showClock", true)
        val clockSize = prefs.getFloat("clockSize", 60f)

        for (id in ids) {
            val views = RemoteViews(context.packageName, R.layout.glance_widget)

            // Update temperature & condition
            val temp = prefs.getString("temperatureCurrentPill", "--")
            val code = prefs.getString("weather_codeCurrentPill", "--")
            val conditionName = prefs.getString("locationCurrentConditon", "--")
            val rawIsDay = prefs.all["isDayWidget"]
            val isDay = when (rawIsDay) {
                is String -> rawIsDay
                is Boolean -> if (rawIsDay) "1" else "0"
                else -> "1"
            }

            views.setTextViewText(R.id.current_glance_temp, "$temp° • ")
            views.setTextViewText(R.id.current_glance_condition, conditionName)

            val iconRes = WeatherIconMapper.getIconResource(code, isDay)
            views.setImageViewResource(R.id.current_img_glance, iconRes)


            if (showClock) {
                views.setViewVisibility(R.id.widget_text_clock_glance, View.VISIBLE)
                views.setTextViewTextSize(R.id.widget_text_clock_glance, TypedValue.COMPLEX_UNIT_SP, clockSize)

                val clockIntent = Intent(AlarmClock.ACTION_SHOW_ALARMS)
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    clockIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_text_clock_glance, pendingIntent)
            } else {
                views.setViewVisibility(R.id.widget_text_clock_glance, View.GONE)
            }



            val calendarUri = CalendarContract.CONTENT_URI.buildUpon().appendPath("time").build()
            val calendarIntent = Intent(Intent.ACTION_VIEW).apply {
                data = calendarUri
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            val calendarPendingIntent = PendingIntent.getActivity(
                context,
                1,
                calendarIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.current_widget_date, calendarPendingIntent)

            val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root_glance, pendingIntent)

            manager.updateAppWidget(id, views)
        }
    }
}
