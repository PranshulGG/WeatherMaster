package com.pranshulgg.weather_master_app.data.worker.notification

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.pranshulgg.weather_master_app.R
import okhttp3.internal.platform.PlatformRegistry.applicationContext


object WeatherNotification {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showNotification(locationName: String?) {

        val contentText =
            if (locationName != null) "Updating weather for $locationName" else "Updating weather"

        val notification = applicationContext?.let {
            NotificationCompat.Builder(
                it,
                WeatherNotificationConfig.CHANNEL_ID
            )
        }
            ?.setContentTitle("WeatherMaster")
            ?.setContentText(contentText)
            ?.setProgress(0, 0, true)
            ?.setOngoing(true)
            ?.setSmallIcon(R.drawable.cloud_download_24px)
            ?.build()

        applicationContext?.let {
            if (notification != null)
                NotificationManagerCompat.from(it)
                    .notify(
                        WeatherNotificationConfig.NOTIFICATION_ID,
                        notification
                    )
        }
    }

    fun hideNotification() {
        applicationContext?.let {
            NotificationManagerCompat
                .from(it)
                .cancel(WeatherNotificationConfig.NOTIFICATION_ID)
        }
    }
}