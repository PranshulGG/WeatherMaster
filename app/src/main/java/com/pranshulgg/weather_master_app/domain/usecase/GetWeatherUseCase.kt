package com.pranshulgg.weather_master_app.domain.usecase

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityResult
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResult
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import com.pranshulgg.weather_master_app.data.repository.data.SourceDataRepository
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val locationsRepo: LocationsRepository,
    private val sourceDataRepository: SourceDataRepository
) {
    suspend operator fun invoke(
        location: Location,
        isManualRefresh: Boolean = false,
        isForceRefresh: Boolean = false,
        isForceRefreshForAirQuality: Boolean = false,
        isForceRefreshForAlerts: Boolean = false,
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