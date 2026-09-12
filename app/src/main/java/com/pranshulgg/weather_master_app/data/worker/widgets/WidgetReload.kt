package com.pranshulgg.weather_master_app.data.worker.widgets

import android.content.Context
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.prefs.helper.PreferencesHelper
import com.pranshulgg.weather_master_app.data.provider.SourceRepositoryProvider
import com.pranshulgg.weather_master_app.data.repository.WeatherContextRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherUnitsRepository
import com.pranshulgg.weather_master_app.data.repository.data.SourceDataRepository
import jakarta.inject.Inject

class WidgetReload @Inject constructor(
    private val weatherContextRepository: WeatherContextRepository,
    private val weatherUnitsRepository: WeatherUnitsRepository,
    private val sourceDataRepository: SourceDataRepository
) {

    suspend fun reload(context: Context) {
        PreferencesHelper.init(context)

        val locations = weatherContextRepository.getLocationsOnce()
        val defaultLocation = locations.find { it.isDefault }
        val units = weatherUnitsRepository.getUnitsOnce()

        if (defaultLocation == null || units == null) return


        var data: Weather? = null

        sourceDataRepository.getData(
            location = defaultLocation,
            isManualRefresh = false,
            onAlerts = {},
            onAirQuality = {},
            onWeather = {
                if (it is WeatherResult.RefreshNotAvailable) {
                    data = it.weather
                } else if (it is WeatherResult.Success) {
                    data = it.weather
                }
            }
        )


        val weather = data ?: return

        val json = widgetWeatherMapper(weather, context, units)
        WeatherWidgetUpdater(context).update(json)
    }
}