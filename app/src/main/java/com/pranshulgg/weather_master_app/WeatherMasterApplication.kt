package com.pranshulgg.weather_master_app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.Configuration
import com.pranshulgg.weather_master_app.data.worker.AppVisibility
import com.pranshulgg.weather_master_app.data.worker.notification.BackgroundUpdateNotificationConfig
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class WeatherMasterApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var visibilityTracker: AppVisibility

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()


    override fun onCreate() {
        super.onCreate()

        ProcessLifecycleOwner
            .get()
            .lifecycle
            .addObserver(visibilityTracker)

        val manager = getSystemService(NotificationManager::class.java)

        manager.createNotificationChannel(
            NotificationChannel(
                BackgroundUpdateNotificationConfig.CHANNEL_ID,
                "WeatherMaster Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )

        manager.createNotificationChannel(
            NotificationChannel(
                BackgroundUpdateNotificationConfig.ERROR_CHANNEL_ID,
                "WeatherMaster Update Errors",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )

    }


}