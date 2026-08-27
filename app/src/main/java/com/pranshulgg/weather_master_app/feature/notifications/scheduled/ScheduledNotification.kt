package com.pranshulgg.weather_master_app.feature.notifications.scheduled

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.ListenableWorker
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.prefs.helper.PreferencesHelper
import com.pranshulgg.weather_master_app.data.provider.SourceRepositoryProvider
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherUnitsRepository
import com.pranshulgg.weather_master_app.feature.notifications.NotificationConfig
import com.pranshulgg.weather_master_app.feature.notifications.isNotificationPermissionGranted
import com.pranshulgg.weather_master_app.feature.notifications.mapper.notificationWeatherMapper
import com.pranshulgg.weather_master_app.feature.notifications.model.NotificationWeatherModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject


@AndroidEntryPoint
class ScheduledNotification : BroadcastReceiver() {


    @Inject
    lateinit var locationsRepository: LocationsRepository

    @Inject
    lateinit var sourceRepositoryProvider: SourceRepositoryProvider

    @Inject
    lateinit var weatherUnitsRepository: WeatherUnitsRepository


    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra("TYPE")
        val time = intent.getLongExtra("TIME", -1L)
        PreferencesHelper.init(context)

        CoroutineScope(Dispatchers.IO).launch {

            val locations = locationsRepository.getLocationsOnce()
            val default = locations.find { it.isDefault }
            val units = weatherUnitsRepository.getUnitsOnce()

            if (default == null || units == null) {
                return@launch
            }

            val repo = sourceRepositoryProvider.getWeatherRepository(default.source)

            val result = repo.getWeather(
                location = default,
                isManualRefresh = false,
                isForceRefresh = false
            )

            if (result is WeatherResult.Success) {
                val weather = result.weather
                val data = notificationWeatherMapper(
                    weather = weather,
                    applicationContext = context,
                    units = units
                )

                if (type == NotificationConfig.TODAY_FORECAST) {
                    showNotification(context, data, true)
                } else {
                    showNotification(context, data, false)
                }
            }

            if (time == -1L) return@launch
            val nextDay = calculateNextDay(time)
            NotificationScheduler.scheduleNotification(context, nextDay, type!!)
        }
    }

    private fun showNotification(
        context: Context,
        weather: NotificationWeatherModel,
        isToday: Boolean
    ) {

        val title = buildString {
            if (isToday) {
                append("Today: ${weather.daily[0].condition} • ${weather.daily[0].maxTemp}/${weather.daily[0].minTemp}")
            } else {
                append("Tomorrow's forecast: ${weather.daily[1].condition} • ${weather.daily[1].maxTemp}/${weather.daily[1].minTemp}")
            }
        }

        val id = if (isToday) {
            NotificationConfig.TODAY_FORECAST_ID
        } else {
            NotificationConfig.NEXT_DAY_FORECAST_ID
        }

        val message = if (isToday) {
            weather.daily[0].summary
        } else {
            weather.daily[1].summary
        }

        val icon = if (isToday) {
            weather.daily[0].conditionIcon
        } else {
            weather.daily[1].conditionIcon
        }

        val largeIconBitmap = BitmapFactory.decodeResource(
            context.resources,
            icon
        )

        val notification = NotificationCompat.Builder(context, NotificationConfig.CHANNEL_ID)
            .setSmallIcon(weather.current.currentConditionIcon)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
                    .setBigContentTitle(title)
            )
            .setLargeIcon(largeIconBitmap)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
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

private fun calculateNextDay(time: Long): Long {
    val instant = Instant.ofEpochMilli(time)
    val nextDayInstant = instant.atZone(ZoneId.systemDefault())
        .plus(1, ChronoUnit.DAYS)
        .toInstant()


    return nextDayInstant.toEpochMilli()

}