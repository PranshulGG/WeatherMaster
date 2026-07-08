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

    const val WORK_NAME = "@pranshulgg_weather_master_updates"
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
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun disableWeatherUpdates(context: Context) {
        WorkManager.getInstance(context)
            .cancelUniqueWork(WORK_NAME)
    }

    suspend fun updateAllWidgets(
        context: Context,
        data: Weather,
        units: WeatherUnits
    ) {
        WeatherWorker.updateAllWidgets(context, data, units)
    }
}