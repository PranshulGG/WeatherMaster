package com.pranshulgg.weather_master_app.core.managers

import android.content.Context
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.data.store.LocationStore
import com.pranshulgg.weather_master_app.data.store.WeatherStore
import com.pranshulgg.weather_master_app.data.store.WeatherUnitsStore
import com.pranshulgg.weather_master_app.data.worker.WeatherBackgroundUpdateScheduler
import com.pranshulgg.weather_master_app.feature.notifications.ongoing.OnGoingNotification
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

// UPDATES NOTIFICATION/WIDGETS
// APP SIDE ONLY!!!
class ExternalManager @Inject constructor(
    private val weatherStore: WeatherStore,
    private val weatherUnitsStore: WeatherUnitsStore,
    @ApplicationContext val context: Context
) {

    val weather = weatherStore.data.value.weather
    val units = weatherUnitsStore.data.value.units

    suspend fun refreshWidgets() {
        if (weather != null) {
            WeatherBackgroundUpdateScheduler.updateAllWidgets(
                context = context,
                data = weather,
                units = units
            )
        }
    }

    fun refreshNotifications() {
        if (weather != null) {
            OnGoingNotification.update(
                context = context,
                weather = weather,
                units = units
            )
        }
    }

}

