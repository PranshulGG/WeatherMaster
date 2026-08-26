package com.pranshulgg.weather_master_app.domain.usecase

/**
 * Initial Clean Architecture Domain Layer integration implemented by https://github.com/gietabhi10
 */

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityResult
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResult
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import com.pranshulgg.weather_master_app.data.repository.data.SourceDataRepository
import javax.inject.Inject

/**
 * Orchestrates the fetching of weather, alerts, and air quality data for a given location.
 */
class GetWeatherUseCase @Inject constructor(
    private val locationsRepo: LocationsRepository,
    private val sourceDataRepository: SourceDataRepository
) {
    /**
     * Executes the weather fetch process.
     *
     * This function should be called from a coroutine scope (e.g., [viewModelScope]).
     * It handles internal suspension and parallel execution of data fetching.
     * Cancellation of the calling coroutine will correctly cancel all internal
     * asynchronous operations.
     *
     * All callbacks are invoked on the dispatcher used by the caller.
     *
     * @param location The location to fetch data for.
     * @param isManualRefresh Whether this is a user-initiated refresh (e.g., pull-to-refresh).
     * @param isForceRefresh Whether to bypass caches for weather data.
     * @param isForceRefreshForAirQuality Whether to bypass caches for air quality data.
     * @param isForceRefreshForAlerts Whether to bypass caches for alerts.
     * @param onLocationUpdated Callback invoked immediately if the device location's coordinates are updated.
     * @param onWeather Callback invoked when weather data is successfully fetched or fails.
     * @param onAlerts Callback invoked when alerts are fetched.
     * @param onAirQuality Callback invoked when air quality data is fetched.
     */
    suspend operator fun invoke(
        location: Location,
        isManualRefresh: Boolean = false,
        isForceRefresh: Boolean = false,
        isForceRefreshForAirQuality: Boolean = false,
        isForceRefreshForAlerts: Boolean = false,
        onLocationUpdated: suspend (Location) -> Unit = {},
        onWeather: suspend (WeatherResult, Location) -> Unit,
        onAlerts: suspend (AlertResult?) -> Unit,
        onAirQuality: suspend (AirQualityResult?) -> Unit,
    ) {
        var effectiveLocation = location
        var effectiveForceRefresh = isForceRefresh
        var effectiveForceRefreshForAirQuality = isForceRefreshForAirQuality
        var effectiveForceRefreshForAlerts = isForceRefreshForAlerts

        if (location.isDeviceLocation) {
            val positionChanged = locationsRepo.updateDeviceLocationPosition()
            if (positionChanged) {
                effectiveLocation = locationsRepo.getLocationForId(location.id)
                effectiveForceRefresh = true
                effectiveForceRefreshForAirQuality = true
                effectiveForceRefreshForAlerts = true
                onLocationUpdated(effectiveLocation)
            }
        }

        sourceDataRepository.getData(
            location = effectiveLocation,
            isManualRefresh = isManualRefresh,
            isForceRefresh = effectiveForceRefresh,
            isForceRefreshForAirQuality = effectiveForceRefreshForAirQuality,
            isForceRefreshForAlerts = effectiveForceRefreshForAlerts,
            onWeather = { result ->
                onWeather(result, effectiveLocation)
            },
            onAlerts = onAlerts,
            onAirQuality = onAirQuality
        )
    }
}