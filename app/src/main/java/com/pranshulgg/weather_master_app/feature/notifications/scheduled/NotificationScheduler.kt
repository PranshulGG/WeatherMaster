package com.pranshulgg.weather_master_app.feature.notifications.scheduled

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.net.toUri
import com.pranshulgg.weather_master_app.feature.notifications.NotificationConfig

object NotificationScheduler {

    fun scheduleNotification(context: Context, time: Long, type: String) {

        val alarmManager = getSystemService(context, AlarmManager::class.java)

        val id = when (type) {
            NotificationConfig.TODAY_FORECAST -> NotificationConfig.TODAY_FORECAST_ID
            NotificationConfig.NEXT_DAY_FORECAST -> NotificationConfig.NEXT_DAY_FORECAST_ID
            else -> throw IllegalArgumentException("Invalid")
        }


        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            Intent(context, ScheduledNotification::class.java).apply {
                putExtra("TYPE", type)
                putExtra("TIME", time)
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager?.canScheduleExactAlarms() == true) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    time,
                    pendingIntent,

                    )
            } else {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = "package:${context.packageName}".toUri()
                }
                context.startActivity(intent)
            }
        } else {
            alarmManager?.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent
            )
        }

    }


    fun cancelScheduledNotification(type: String, context: Context) {
        val alarmManager = getSystemService(context, AlarmManager::class.java)

        val id = when (type) {
            NotificationConfig.TODAY_FORECAST -> NotificationConfig.TODAY_FORECAST_ID
            NotificationConfig.NEXT_DAY_FORECAST -> NotificationConfig.NEXT_DAY_FORECAST_ID
            else -> throw IllegalArgumentException("Invalid")
        }


        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            Intent(context, ScheduledNotification::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

}