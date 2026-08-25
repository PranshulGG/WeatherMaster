package com.pranshulgg.weather_master_app.data.worker.notification

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.pranshulgg.weather_master_app.R


object BackgroundWeatherUpdateNotification {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showNotification(locationName: String?, context: Context) {

        val contentText =
            if (locationName != null) "Updating weather for $locationName" else "Updating weather"

        val notification = context.let {
            NotificationCompat.Builder(
                it,
                BackgroundUpdateNotificationConfig.CHANNEL_ID
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
                    BackgroundUpdateNotificationConfig.NOTIFICATION_ID,
                    notification
                )
        }
    }

    fun hideNotification(context: Context) {
        NotificationManagerCompat
            .from(context)
            .cancel(
                BackgroundUpdateNotificationConfig.NOTIFICATION_ID
            )
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showErrorNotification(error: String?, context: Context) {


        val notification = context.let {
            NotificationCompat.Builder(
                it,
                BackgroundUpdateNotificationConfig.ERROR_CHANNEL_ID
            )
        }
            .setContentTitle("Background update failed")
            .setContentText(error ?: "Unknown error")
            .setSmallIcon(R.drawable.info_24px)
            .build()

        context.let {
            NotificationManagerCompat.from(it)
                .notify(
                    BackgroundUpdateNotificationConfig.ERROR_NOTIFICATION_ID,
                    notification
                )
        }
    }

}