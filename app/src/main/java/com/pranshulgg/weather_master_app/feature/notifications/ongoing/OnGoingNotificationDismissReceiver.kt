package com.pranshulgg.weather_master_app.feature.notifications.ongoing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.pranshulgg.weather_master_app.core.prefs.helper.PreferencesHelper
import com.pranshulgg.weather_master_app.data.repository.WeatherContextRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherUnitsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Android 14+ lets users swipe away FLAG_ONGOING_EVENT notifications (see #1214),
 * so this re-posts the ongoing weather notification from cache as soon as it's
 * dismissed instead of waiting for the next periodic WeatherWorker refresh.
 */
@AndroidEntryPoint
class OnGoingNotificationDismissReceiver : BroadcastReceiver() {

    @Inject
    lateinit var weatherContextRepository: WeatherContextRepository

    @Inject
    lateinit var weatherUnitsRepository: WeatherUnitsRepository

    override fun onReceive(context: Context, intent: Intent) {
        PreferencesHelper.init(context)

        val isOnGoingNotificationEnabled = PreferencesHelper.getBool(
            "isOnGoingNotificationEnabled"
        ) ?: false

        if (!isOnGoingNotificationEnabled) return

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val locations = weatherContextRepository.getLocationsOnce()
                val default = locations.find { it.isDefault } ?: return@launch
                val units = weatherUnitsRepository.getUnitsOnce() ?: return@launch
                val weather = weatherContextRepository.getWeatherForLocation(default.id)
                    ?: return@launch

                OnGoingNotification.update(weather, context, units)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
