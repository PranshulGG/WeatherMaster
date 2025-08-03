package com.pranshulgg.weather_master_app


import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import android.content.ComponentName

class CurrentWeatherWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        for (id in ids) {
            val views = RemoteViews(context.packageName, R.layout.widget_current_weather)
            views.setTextViewText(R.id.current_temp, "--°C")
            views.setImageViewResource(R.id.weather_icon, R.drawable.cloudy)

            manager.updateAppWidget(id, views)
        }
    }
}
