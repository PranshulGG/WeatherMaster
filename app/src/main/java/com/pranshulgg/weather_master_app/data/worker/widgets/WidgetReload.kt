package com.pranshulgg.weather_master_app.data.worker.widgets

import android.content.Context
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.data.provider.WeatherRepositoryProvider
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherUnitsRepository
import jakarta.inject.Inject

class WidgetReload @Inject constructor(
    private val repositoryProvider: WeatherRepositoryProvider,
    private val locationsRepository: LocationsRepository,
    private val weatherUnitsRepository: WeatherUnitsRepository
) {

    suspend fun reload(context: Context) {
        val locations = locationsRepository.getLocationsOnce()
        val defaultLocation = locations.find { it.isDefault }
        val units = weatherUnitsRepository.getUnitsOnce()

        if (defaultLocation == null || units == null) return

        val repo = repositoryProvider.getRepository(defaultLocation.source)

        val result = repo.getWeather(
            location = defaultLocation,
            isManualRefresh = false,
            isForceRefresh = false
        )

        val weather = (result as? WeatherResult.Success)?.weather ?: return

        val json = widgetWeatherMapper(weather, context, units)
        WeatherWidgetUpdater(context).update(json)
    }
}