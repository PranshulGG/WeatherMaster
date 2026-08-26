package com.pranshulgg.weather_master_app.feature.notifications.ongoing

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Icon
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.feature.notifications.NotificationConfig
import com.pranshulgg.weather_master_app.feature.notifications.mapper.notificationWeatherMapper
import com.pranshulgg.weather_master_app.feature.notifications.model.NotificationWeatherModel
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.IconCompat
import com.pranshulgg.weather_master_app.feature.notifications.isNotificationPermissionGranted

object OnGoingNotification {

    fun update(weather: Weather, context: Context, units: WeatherUnits) {

        val data = notificationWeatherMapper(weather, context, units)


        val views = RemoteViews(context.packageName, R.layout.notification_weather)
        val viewsSmall = RemoteViews(context.packageName, R.layout.notification_weather_small)
        val locationName = weather.location.name

        populateCurrent(views, data, locationName)
        populateCurrent(viewsSmall, data, locationName)



        data.hourly.take(6).forEach { hour ->

            val hourView = RemoteViews(
                context.packageName,
                R.layout.notification_weather_hour
            )

            hourView.setTextViewText(
                R.id.hour,
                hour.time
            )

            hourView.setTextViewText(
                R.id.temperature,
                hour.temp
            )

            hourView.setImageViewResource(
                R.id.icon,
                hour.conditionIcon
            )

            views.addView(
                R.id.hourlyContainer,
                hourView
            )
        }

        val iconBitmap = createTextBitmap(data.current.temp)
        val smallIcon = IconCompat.createWithBitmap(iconBitmap!!)

        val notification = NotificationCompat.Builder(
            context,
            NotificationConfig.CHANNEL_ID
        ).setSmallIcon(smallIcon)
            .setCustomContentView(viewsSmall)
            .setCustomBigContentView(views)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()


        
        @SuppressLint("MissingPermission")
        if (context.isNotificationPermissionGranted()) {
            NotificationManagerCompat
                .from(context)
                .notify(NotificationConfig.ONGOING_NOTIFICATION_ID, notification)

        } else {
            NotificationManagerCompat
                .from(context)
                .notify(NotificationConfig.ONGOING_NOTIFICATION_ID, notification)
        }
    }


    fun remove(context: Context) {
        NotificationManagerCompat
            .from(context)
            .cancel(NotificationConfig.ONGOING_NOTIFICATION_ID)
    }
}

private fun populateCurrent(
    views: RemoteViews,
    data: NotificationWeatherModel,
    locationName: String
) {
    val currentTemp = data.current.temp
    val currentIcon = data.current.currentConditionIcon
    val currentCondition = data.current.currentCondition
    views.setTextViewText(R.id.currentTemp, currentTemp)

    views.setTextViewText(R.id.currentConditionLabel, currentCondition)
    views.setImageViewResource(R.id.currentIcon, currentIcon)


    views.setTextViewText(
        R.id.location,
        locationName
    )

}

private fun createTextBitmap(text: String?): Bitmap? {

    if (text.isNullOrBlank()) return null

    val width = 86
    val height = 86
    val bitmap = createBitmap(width, height)
    val canvas = Canvas(bitmap)

    val paint = Paint().apply {
        color = Color.WHITE
        textSize = 68f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    val bounds = Rect()
    paint.getTextBounds(text, 0, text.length, bounds)
    val x = width / 2f
    val y = height / 2f + (bounds.height() / 2f)

    canvas.drawText(text, x, y, paint)
    return bitmap
}
