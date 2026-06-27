package com.pranshulgg.weather_master_app.data.worker.notification

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.pranshulgg.weather_master_app.R


object WeatherNotification {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showNotification(locationName: String?, context: Context) {

        val contentText =
            if (locationName != null) "Updating weather for $locationName" else "Updating weather"

        val notification = context.let {
            NotificationCompat.Builder(
                it,
                WeatherNotificationConfig.CHANNEL_ID
            )
        }
            .setContentTitle("WeatherMaster")
            .setContentText(contentText)
            .setProgress(0, 0, true)
            .setOngoing(true)
            .setSmallIcon(R.drawable.cloud_download_24px)
            .build()

        context.let {
            NotificationManagerCompat.from(it)
                .notify(
                    WeatherNotificationConfig.NOTIFICATION_ID,
                    notification
                )
        }
    }

    fun hideNotification(context: Context) {
        NotificationManagerCompat
            .from(context)
            .cancel(
                WeatherNotificationConfig.NOTIFICATION_ID
            )
    }
}