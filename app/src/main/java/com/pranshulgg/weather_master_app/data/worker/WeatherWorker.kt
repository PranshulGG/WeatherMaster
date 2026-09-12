package com.pranshulgg.weather_master_app.data.worker

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.prefs.helper.PreferencesHelper
import com.pranshulgg.weather_master_app.data.provider.SourceRepositoryProvider
import com.pranshulgg.weather_master_app.data.repository.WeatherContextRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherUnitsRepository
import com.pranshulgg.weather_master_app.data.repository.data.SourceDataRepository
import com.pranshulgg.weather_master_app.data.worker.gadgetbridge.sendGadgetBridgeWeatherData
import com.pranshulgg.weather_master_app.data.worker.notification.BackgroundWeatherUpdateNotification
import com.pranshulgg.weather_master_app.data.worker.notification.BackgroundWeatherUpdateNotification.showErrorNotification
import com.pranshulgg.weather_master_app.data.worker.widgets.WeatherWidgetUpdater
import com.pranshulgg.weather_master_app.data.worker.widgets.widgetWeatherMapper
import com.pranshulgg.weather_master_app.feature.notifications.ongoing.OnGoingNotification
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class WeatherWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val weatherContextRepository: WeatherContextRepository,
    private val appVisibility: AppVisibility,
    private val weatherUnitsRepository: WeatherUnitsRepository,
    private val sourceDataRepository: SourceDataRepository
) : CoroutineWorker(context, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {

        // Only run if app is backgrounded
        if (appVisibility.isForeground) {
            return Result.success()
        }

        PreferencesHelper.init(applicationContext)
        return try {

            val sendDataToGadgetBridge = PreferencesHelper.getBool(
                "isSendDataToGadgetbridge"
            ) ?: false
            val isOnGoingNotificationEnabled = PreferencesHelper.getBool(
                "isOnGoingNotificationEnabled"
            ) ?: false


            // Get the locations and units
            val locations = weatherContextRepository.getLocationsOnce()
            val default = locations.find { it.isDefault }
            val units = weatherUnitsRepository.getUnitsOnce()


            if (default == null || units == null) {
                return Result.success()
            }

            /**
             * Show a notification whenever the worker runs
             * Don't really need it but why not, i wanna know if its working
             */
            BackgroundWeatherUpdateNotification.showNotification(default.name, applicationContext)


            var weather: Weather? = null

            sourceDataRepository.getData(
                location = default,
                isManualRefresh = true,
                onAlerts = {},
                onAirQuality = {},
                onWeather = {
                    if (it is WeatherResult.RefreshNotAvailable) {
                        weather = it.weather
                    } else if (it is WeatherResult.Success) {
                        weather = it.weather
                    }
                }
            )

            if (weather == null) {
                return Result.success()
            }

            if (sendDataToGadgetBridge) {
                sendGadgetBridgeWeatherData(applicationContext, weather)
            }

            if (isOnGoingNotificationEnabled) {
                OnGoingNotification.update(weather, applicationContext, units)
            }

            updateAllWidgets(applicationContext, weather, units)

            PreferencesHelper.setLong("LAST_WORKER_SUCCESS_RUN", System.currentTimeMillis())

            return Result.success()

        } catch (e: Exception) {
            showErrorNotification(e.message, applicationContext)
            Result.failure()
        } finally {
            BackgroundWeatherUpdateNotification.hideNotification(applicationContext)
        }
    }

    companion object {

        suspend fun updateAllWidgets(
            context: Context,
            data: Weather,
            units: WeatherUnits,
        ) {
            val json = widgetWeatherMapper(data, context, units)

            WeatherWidgetUpdater(context).update(json)
        }

    }
}