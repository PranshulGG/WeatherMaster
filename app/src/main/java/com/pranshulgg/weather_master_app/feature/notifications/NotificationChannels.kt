package com.pranshulgg.weather_master_app.feature.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.data.worker.notification.BackgroundUpdateNotificationConfig

object NotificationChannels {

    fun createAll(context: Context) {
        val notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        notificationManager.createNotificationChannel(
            NotificationChannel(
                NotificationConfig.CHANNEL_ID,
                context.getString(R.string.weather),
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )

        notificationManager.createNotificationChannel(
            NotificationChannel(
                BackgroundUpdateNotificationConfig.CHANNEL_ID,
                context.getString(R.string.setting_background_updates),
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )

        notificationManager.createNotificationChannel(
            NotificationChannel(
                BackgroundUpdateNotificationConfig.ERROR_CHANNEL_ID,
                context.getString(R.string.error_failed_background_updates),
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )
    }

}