package com.pranshulgg.weather_master_app.feature.settings.background

import android.content.Context
import androidx.lifecycle.ViewModel
import com.pranshulgg.weather_master_app.data.worker.WeatherBackgroundUpdateScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


@HiltViewModel
class BackgroundUpdatesViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    fun scheduleWeatherUpdates(minutes: Int) {
        WeatherBackgroundUpdateScheduler.scheduleWeatherUpdates(context, repeatInterval = minutes)
    }

    fun disableWeatherUpdates() {
        WeatherBackgroundUpdateScheduler.disableWeatherUpdates(context)
    }
}