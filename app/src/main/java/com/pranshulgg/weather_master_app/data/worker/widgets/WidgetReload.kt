package com.pranshulgg.weather_master_app.data.worker.widgets

import android.content.Context
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.prefs.helper.PreferencesHelper
import com.pranshulgg.weather_master_app.data.provider.SourceRepositoryProvider
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherUnitsRepository
import com.pranshulgg.weather_master_app.data.worker.widgets.RainWidgetStateBuilder
import com.pranshulgg.weather_master_app.widgets.model.RainWidgetState
import jakarta.inject.Inject

class WidgetReload @Inject constructor(
    private val repositoryProvider: SourceRepositoryProvider,
    private val locationsRepository: LocationsRepository,
    private val weatherUnitsRepository: WeatherUnitsRepository,
    private val rainWidgetStateBuilder: RainWidgetStateBuilder
) {

    suspend fun reload(context: Context) {
        PreferencesHelper.init(context)

        val locations = locationsRepository.getLocationsOnce()
        val defaultLocation = locations.find { it.isDefault }
        val units = weatherUnitsRepository.getUnitsOnce()

        if (defaultLocation == null || units == null) return

        val repo = repositoryProvider.getWeatherRepository(defaultLocation.source)

        val result = repo.getWeather(
            location = defaultLocation,
            isManualRefresh = false,
            isForceRefresh = false
        )

        val weather = (result as? WeatherResult.Success)?.weather ?: return

        val json = widgetWeatherMapper(weather, context, units)

        val rainJson = try {
            rainWidgetStateBuilder.build(context, defaultLocation, weather, units)?.let { state ->
                kotlinx.serialization.json.Json.encodeToString(
                    RainWidgetState.serializer(),
                    state
                )
            }
        } catch (e: Exception) {
            null
        }

        WidgetWeatherUpdater(context).update(json, rainJson)
    }
}