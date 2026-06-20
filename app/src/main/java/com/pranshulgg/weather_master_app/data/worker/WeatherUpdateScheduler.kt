package com.pranshulgg.weather_master_app.data.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import java.util.concurrent.TimeUnit

object WeatherUpdateScheduler {
    fun scheduleWeatherUpdates(
        context: Context,
        repeatInterval: Int,
    ) {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request =
            PeriodicWorkRequestBuilder<WeatherWorker>(
                repeatInterval.toLong(),
                TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()


        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "@pranshulgg_weather_master_updates",
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request
        )
    }

    fun disableWeatherUpdates(context: Context) {
        WorkManager.getInstance(context)
            .cancelUniqueWork("@pranshulgg_weather_master_updates")
    }

    suspend fun updateAllWidgets(
        context: Context,
        skipForegroundCheck: Boolean = false,
        data: Weather,
        units: WeatherUnits
    ) {
        WeatherWorker.updateAllWidgets(context, data, skipForegroundCheck, units)
    }
}