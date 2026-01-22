package com.pranshulgg.weather_master_app

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import com.pranshulgg.weather_master_app.util.WeatherIconMapper

class WeatherPersistentNotificationService : Service() {

    companion object {
        const val CHANNEL_ID = "WeatherPersistentChannel"
        const val NOTIFICATION_ID = 424242
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, buildNotification())
        return START_STICKY
    }

    override fun onDestroy() {
        stopForeground(true)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(): Notification {
        val prefs = getSharedPreferences(
            "HomeWidgetPreferences",
            Context.MODE_PRIVATE
        )

        val temp = prefs.getString("temperatureCurrentPill", "--")
        val condition = prefs.getString("locationCurrentConditon", "--")

        val rawIsDay = prefs.all["isDayWidget"]

        val isDay = when (rawIsDay) {
            is String -> rawIsDay
            is Boolean -> if (rawIsDay) "1" else "0"
            else -> "1"
        }

        val code = prefs.getString("weather_codeCurrentPill", "--")

        val min = prefs.getString("todayMin", "--")
        val max = prefs.getString("todayMax", "--")


        val iconRes = WeatherIconMapper.getIconResource(code, isDay)
        val largeIcon = BitmapFactory.decodeResource(resources, iconRes)


        val intent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val tempIcon = createTempIcon(temp ?: "--")

        return NotificationCompat.Builder(this, CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_stat_cloud_download)
            .setSmallIcon(tempIcon)
            .setContentTitle("$temp° • $condition")
            .setContentText("H: $max° • L: $min°")
            .setLargeIcon(largeIcon)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Persistent Weather",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }
}

private fun createTempIcon(temp: String): IconCompat {
    val size = 128
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 96f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    val x = size / 2f
    val y = size / 2f - (paint.descent() + paint.ascent()) / 2

    canvas.drawText("${temp}°", x, y, paint)

    return IconCompat.createWithBitmap(bitmap)
}

