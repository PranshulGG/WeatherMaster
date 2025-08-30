package com.pranshulgg.weather_master_app

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Bundle
import android.widget.RemoteViews
import android.app.PendingIntent
import android.content.Intent
import com.pranshulgg.weather_master_app.util.WeatherIconMapper

class PillWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        for (id in ids) {
            updateWidget(context, manager, id)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        updateWidget(context, appWidgetManager, appWidgetId)
    }

    private fun updateWidget(context: Context, manager: AppWidgetManager, id: Int) {
        val options = manager.getAppWidgetOptions(id)
        val minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        val minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
        val layoutId = when {
            minWidth > 100 && minHeight < 100 -> R.layout.pill_widget_mid

            minWidth > 300 -> R.layout.pill_widget_large

            minWidth > 150 &&  minWidth <= 300 && minHeight > 150 &&  minHeight <= 300 -> R.layout.pill_widget_mid_vert


            else -> R.layout.pill_widget
        }


        val views = RemoteViews(context.packageName, layoutId)

        val prefs = context.getSharedPreferences("HomeWidgetPreferences", Context.MODE_PRIVATE)
        val temp = prefs.getString("temperatureCurrentPill", "--")
        val code = prefs.getString("weather_codeCurrentPill", "--")

        val rawIsDay = prefs.all["isDayWidget"]
        val isDay = when (rawIsDay) {
            is String -> rawIsDay
            is Boolean -> if (rawIsDay) "1" else "0"
            else -> "1"
        }

        views.setTextViewText(R.id.current_widget_temp_pill_pixel, "$temp°")
        val iconRes = WeatherIconMapper.getIconResource(code, isDay)
        views.setImageViewResource(R.id.widget_icon_current_pill_pixel, iconRes)

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_root_pill, pendingIntent)

        manager.updateAppWidget(id, views)
    }
}
