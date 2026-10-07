package com.pranshulgg.weather_master_app.feature.notifications.alert

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.pranshulgg.weather_master_app.MainActivity
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertSeverity
import com.pranshulgg.weather_master_app.feature.notifications.NotificationConfig
import com.pranshulgg.weather_master_app.feature.notifications.isNotificationPermissionGranted
import java.util.concurrent.atomic.AtomicInteger


/**
 * Every alert notification is separate
 * Get a unique ID from AtomicInteger for each alert to make sure there are no duplicates
 **/
object AlertNotificationIdHelper {
    val nextId = AtomicInteger(999)
    fun getNextId() = nextId.getAndIncrement()
}


class AlertNotification(private val context: Context) {

    fun show(alert: Alert, id: Int) {

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationConfig.ALERT_CHANNEL_ID)
            .setSmallIcon(R.drawable.warning_24px)
            .setContentTitle(alert.event)
            .setContentText(alert.description)
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(alert.description)
            )
            .setColor(
                ContextCompat.getColor(
                    context,
                    alert.severity?.colorRes ?: AlertSeverity.UNKNOWN.colorRes
                )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .build()

        @SuppressLint("MissingPermission")
        if (context.isNotificationPermissionGranted()) {
            NotificationManagerCompat
                .from(context)
                .notify(id, notification)
        } else {
            NotificationManagerCompat
                .from(context)
                .notify(id, notification)
        }
    }


}

