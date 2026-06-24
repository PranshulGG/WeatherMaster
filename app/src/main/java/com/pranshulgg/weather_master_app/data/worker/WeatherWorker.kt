package com.pranshulgg.weather_master_app.data.worker

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.data.provider.WeatherRepositoryProvider
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherUnitsRepository
import com.pranshulgg.weather_master_app.data.worker.notification.WeatherNotification
import com.pranshulgg.weather_master_app.data.worker.widgets.WeatherWidgetUpdater
import com.pranshulgg.weather_master_app.data.worker.widgets.widgetWeatherMapper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class WeatherWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repositoryProvider: WeatherRepositoryProvider,
    private val locationsRepository: LocationsRepository,
    private val appVisibility: AppVisibility,
    private val weatherUnitsRepository: WeatherUnitsRepository
) : CoroutineWorker(context, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {

        val skipForegroundCheck =
            inputData.getBoolean(KEY_SKIP_FOREGROUND_CHECK, false)

        // Only run if app is backgrounded
        if (appVisibility.isForeground) {
            return Result.success()
        }

        return try {


            // Get the locations and units
            val locations = locationsRepository.getLocationsOnce()
            val default = locations.find { it.isDefault }
            val units = weatherUnitsRepository.getUnitsOnce()

            if (default == null || units == null) {
                return Result.success()
            }

            /**
             * Show a notification whenever the worker runs
             * Don't really need it but why not, i wanna know if its working
             */
            WeatherNotification.showNotification(default.name)


            // Get the repository
            val repo = repositoryProvider.getRepository(default.source)


            val result = repo.getWeather(
                location = default,
                isManualRefresh = true,
                isForceRefresh = false
            )


            if (result !is WeatherResult.Success) {
                return Result.success()
            }

            val weather = result.weather


            val json = widgetWeatherMapper(weather, applicationContext, units)


            WeatherWidgetUpdater(applicationContext).update(json)

            return Result.success()

        } catch (e: Exception) {
            Result.failure()
        } finally {
            WeatherNotification.hideNotification()
        }
    }

    companion object {
        const val KEY_SKIP_FOREGROUND_CHECK = "skip_foreground_check"

        suspend fun updateAllWidgets(
            context: Context,
            data: Weather,
            skipForegroundCheck: Boolean = false,
            units: WeatherUnits
        ) {
            val json = widgetWeatherMapper(data, context, units)

            WeatherWidgetUpdater(context).update(json)
        }

        fun runWorkerOnce(context: Context) {
            val request = OneTimeWorkRequestBuilder<WeatherWorker>()
                .setInitialDelay(0, TimeUnit.SECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "@pranshulgg_weather_master_updates_once",
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }
}