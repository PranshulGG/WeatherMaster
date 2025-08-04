package com.pranshulgg.weather_master_app

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class WidgetUpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val temp = intent.getIntExtra("temp", 0)
        val iconCode = intent.getIntExtra("iconCode", 1)

        val views = RemoteViews(context.packageName, R.layout.widget_current_weather)
        views.setTextViewText(R.id.current_temp, "$temp°C")

        val iconRes = when (iconCode) {
            1 -> R.drawable.sunny
            2 -> R.drawable.cloudy
            3 -> R.drawable.heavy_rain
            else -> R.drawable.sunny
        }

        views.setImageViewResource(R.id.weather_icon, iconRes)

        val manager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, CurrentWeatherWidget::class.java)
        manager.updateAppWidget(componentName, views)
    }
}
