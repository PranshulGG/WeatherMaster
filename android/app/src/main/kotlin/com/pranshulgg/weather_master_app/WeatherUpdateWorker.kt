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
        private const val NOTIFICATION_ID = 99999999
    }

    private var flutterEngine: FlutterEngine? = null


    override suspend fun doWork(): Result {
        setForeground(createForegroundInfo())

        return try {
            withContext(Dispatchers.Main) {
                val loader = FlutterInjector.instance().flutterLoader()
                if (!loader.initialized()) {
                    loader.startInitialization(applicationContext)
                    loader.ensureInitializationComplete(applicationContext, null)
                }

                flutterEngine = FlutterEngine(applicationContext).apply {
                    dartExecutor.executeDartEntrypoint(
                        DartExecutor.DartEntrypoint(
                            loader.findAppBundlePath(),
                            "workerUpdateWidget"
                        )
                    )
                }

                Log.d("WeatherWorker", "Dart entrypoint launched")
            }

            delay(6000)

            withContext(Dispatchers.Main) {
                flutterEngine?.destroy()
            }

            val prefs = applicationContext.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
            prefs.edit().putLong("last_weather_update", System.currentTimeMillis()).apply()

            Result.success()
        } catch (e: Exception) {
            Log.e("WeatherWorker", "Error in worker", e)
            withContext(Dispatchers.Main) {
                flutterEngine?.destroy()
            }
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
