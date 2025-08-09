package com.pranshulgg.weather_master_app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.FlutterInjector
import android.content.pm.ServiceInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class WeatherUpdateWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val CHANNEL_ID = "WeatherUpdateChannel"
        private const val NOTIFICATION_ID = 9
    }

    private var flutterEngine: FlutterEngine? = null


    override suspend fun doWork(): Result {

        setForeground(createForegroundInfo())
    



        return try {
            withContext(Dispatchers.Main) {
                // Start a temporary Flutter engine with entrypoint workerUpdateWidget
                flutterEngine = FlutterEngine(applicationContext).apply {
                    dartExecutor.executeDartEntrypoint(
                        DartExecutor.DartEntrypoint(
                            FlutterInjector.instance().flutterLoader().findAppBundlePath(),
                            "workerUpdateWidget" // matches @pragma entrypoint in Dart
                        )
                    )
                }

                // Wait to let Dart run updateHomeWidget()
                delay(6000)

                flutterEngine?.destroy()
            }


             delay(6000)
            Result.success()

        } catch (e: Exception) {
            flutterEngine?.destroy()
            Result.retry()
        }
    }

    private fun createForegroundInfo(): ForegroundInfo {
        createNotificationChannel()

        val notification: Notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Updating weather...")
            .setContentText("")
            .setSmallIcon(R.drawable.ic_stat_cloud_download)
            .setOngoing(true)
            .setProgress(0, 0, true)
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(NOTIFICATION_ID, notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Weather Updates",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = applicationContext.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
