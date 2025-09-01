package com.pranshulgg.weather_master_app

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import android.app.PendingIntent
import android.content.Intent
import android.provider.AlarmClock
import android.provider.CalendarContract
import com.pranshulgg.weather_master_app.util.WeatherIconMapper

class ClockHourlyWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        for (id in ids) {
            updateWidget(context, manager, id, null)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        manager: AppWidgetManager,
        id: Int,
        newOptions: Bundle?
    ) {
        updateWidget(context, manager, id, newOptions)
        super.onAppWidgetOptionsChanged(context, manager, id, newOptions)
    }

    private fun updateWidget(
        context: Context,
        manager: AppWidgetManager,
        id: Int,
        newOptions: Bundle?
    ) {
        val views = RemoteViews(context.packageName, R.layout.clock_date_hourly)
        val prefs = context.getSharedPreferences("HomeWidgetPreferences", Context.MODE_PRIVATE)
        val temp = prefs.getString("temperatureCurrentPill", "--")
        val code = prefs.getString("weather_codeCurrentPill", "--")
        val conditionName = prefs.getString("locationCurrentConditon", "--")
        val locationName = prefs.getString("locationNameWidget", "--")

        val rawIsDay = prefs.all["isDayWidget"]
        val isDay = when (rawIsDay) {
            is String -> rawIsDay
            is Boolean -> if (rawIsDay) "1" else "0"
            else -> "1"
        }

        val max = prefs.getString("todayMax", "--")
        val min = prefs.getString("todayMin", "--")

        views.setTextViewText(R.id.current_widget_temp_cast_clock_hourly, "$temp°")
//        views.setTextViewText(R.id.temp_high_cast_clock_hourly, "$max° •")
//        views.setTextViewText(R.id.temp_low_cast_clock_hourly, " $min°")

//        val iconRes = WeatherIconMapper.getIconResource(code, isDay)
//        views.setImageViewResource(R.id.widget_icon_hourly_cast_clock_hourly, iconRes)

        val containerId = R.id.hourly_cast_clock_hourly
        views.removeAllViews(containerId)

        val options = newOptions ?: manager.getAppWidgetOptions(id)
        val minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)

        val widgetSize = when {
            minWidth > 300 -> WidgetSize.LARGE
            minWidth > 250 -> WidgetSize.MEDIUM
            else -> WidgetSize.SMALL
        }

        when (widgetSize) {
            WidgetSize.LARGE -> {
                views.setTextViewText(R.id.weather_condition_current_clock_hourly, conditionName)
//                views.setTextViewText(R.id.current_widget_locationName, locationName)
                views.setViewVisibility(R.id.weather_condition_current_clock_hourly, View.VISIBLE)
//                views.setViewVisibility(R.id.current_widget_locationName, View.VISIBLE)
            }
            WidgetSize.MEDIUM -> {
                views.setTextViewText(R.id.weather_condition_current_clock_hourly, conditionName)
                views.setViewVisibility(R.id.weather_condition_current_clock_hourly, View.VISIBLE)
//                views.setViewVisibility(R.id.current_widget_locationName, View.GONE)
            }
            WidgetSize.SMALL -> {
                views.setViewVisibility(R.id.weather_condition_current_clock_hourly, View.GONE)
//                views.setViewVisibility(R.id.current_widget_locationName, View.GONE)
            }
        }

        val numItems = when (widgetSize) {
            WidgetSize.LARGE -> 4
            WidgetSize.MEDIUM -> 2
            WidgetSize.SMALL -> 0
        }

        for (i in 0 until numItems) {
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
        views.setOnClickPendingIntent(R.id.clockTextHourlyDate, calendarPendingIntent)

        val clockIntent = Intent(AlarmClock.ACTION_SHOW_ALARMS)
        val pendingIntentTime = PendingIntent.getActivity(
            context,
            0,
            clockIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.clockTextHourlyTime, pendingIntentTime)

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }


        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        views.setOnClickPendingIntent(R.id.widget_root_cast_clock_hourly, pendingIntent)

        manager.updateAppWidget(id, views)
    }

    private enum class WidgetSize {
        LARGE, MEDIUM, SMALL
    }
}