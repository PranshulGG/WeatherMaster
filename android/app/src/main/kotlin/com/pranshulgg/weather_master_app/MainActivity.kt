package com.pranshulgg.weather_master_app

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import android.widget.RemoteViews
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {
    private val CHANNEL = "weather.widget/channel"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
            call, result ->
            if (call.method == "updateCurrentWidget") {
                val temp = call.argument<Int>("temp") ?: 0
                val iconCode = call.argument<Int>("iconCode") ?: 0
                updateCurrentWeatherWidget(temp, iconCode)
                result.success(null)
            } else {
                result.notImplemented()
            }
        }
    }

private fun updateCurrentWeatherWidget(temp: Int, iconCode: Int) {

    val views = RemoteViews(packageName, R.layout.widget_current_weather)
    views.setTextViewText(R.id.current_temp, "$temp°C")

    val iconRes = when (iconCode) {
        1 -> R.drawable.sunny
        2 -> R.drawable.cloudy
        3 -> R.drawable.heavy_rain
        else -> R.drawable.sunny
    }

    views.setImageViewResource(R.id.weather_icon, iconRes)

    val manager = AppWidgetManager.getInstance(applicationContext)
    val componentName = ComponentName(applicationContext, CurrentWeatherWidget::class.java)
    manager.updateAppWidget(componentName, views)
}

}
