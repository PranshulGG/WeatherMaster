package com.pranshulgg.weather_master_app.core.managers

import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.domain.toMessageRes
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.sources.isGlobal
import com.pranshulgg.weather_master_app.core.model.sources.isSourceSupportedFor
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityResult
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResult
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import com.pranshulgg.weather_master_app.data.repository.data.SourceDataRepository
import com.pranshulgg.weather_master_app.data.store.LocationStore
import com.pranshulgg.weather_master_app.data.store.WeatherStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherManager @Inject constructor(
    private val locationsRepository: LocationsRepository,
    private val sourceDataRepository: SourceDataRepository,
    private val weatherStore: WeatherStore,
    private val locationStore: LocationStore
) {

    private val scope = CoroutineScope(SupervisorJob())

    private val _errors = MutableSharedFlow<AppException>(
        extraBufferCapacity = 1
    )

    val errors = _errors.asSharedFlow()

    private var weatherJob: Job? = null

    /**
     * @param onUnsupportedSourceError Lets the UI know that the source for this location
     * isn't supported in the region
     */
    operator fun invoke(
        location: Location,
        isManualRefresh: Boolean = false,
        isForceRefresh: Boolean = false,
        isForceRefreshForAirQuality: Boolean = false,
        isForceRefreshForAlerts: Boolean = false,
        skipDeviceLocationCheck: Boolean = false,
        onUnsupportedSourceError: () -> Unit = {}
    ) {

        weatherJob?.cancel()

        weatherJob = scope.launch {

            var effectiveLocation = location
            var effectiveForceRefresh = isForceRefresh
            var effectiveForceRefreshForAirQuality = isForceRefreshForAirQuality
            var effectiveForceRefreshForAlerts = isForceRefreshForAlerts

            if (location.isDeviceLocation && !skipDeviceLocationCheck) {
                val positionChanged = locationsRepository.updateDeviceLocationPosition()
                if (positionChanged) {
                    effectiveLocation = locationsRepository.getLocationForId(location.id)
                    effectiveForceRefresh = true
                    effectiveForceRefreshForAirQuality = true
                    effectiveForceRefreshForAlerts = true
                    locationStore.set(effectiveLocation)
                }
            }


            sourceDataRepository.getData(
                location = effectiveLocation,
                isManualRefresh = isManualRefresh,
                isForceRefresh = effectiveForceRefresh,
                isForceRefreshForAirQuality = effectiveForceRefreshForAirQuality,
                isForceRefreshForAlerts = effectiveForceRefreshForAlerts,
                onWeather = { result ->
                    writeWeather(result, onUnsupportedSourceError, effectiveLocation)
                },
                onAlerts = { result ->
                    writeAlerts(result)
                },
                onAirQuality = { result ->
                    writeAirQuality(result)
                },
            )

        }
    }

    private fun writeWeather(
        result: WeatherResult,
        onUnsupportedSourceError: () -> Unit,
        location: Location
    ) {
        when (result) {

            is WeatherResult.Success -> {
                weatherStore.set(weather = result.weather, isWeatherLoaded = true)
            }

            is WeatherResult.Error -> {
                weatherStore.set(weather = result.cacheWeather, isWeatherLoaded = true)


                val isUnsupportedSource = !location.source.isGlobal()
                        && !location.source.isSourceSupportedFor(
                    location.countryCode?.uppercase()
                )

                if (isUnsupportedSource) {
                    onUnsupportedSourceError()
                }

                _errors.tryEmit(result.exception.toAppException())

            }

            is WeatherResult.RefreshNotAvailable -> {
                _errors.tryEmit(AppException.RefreshNotAvailable())
            }
        }
    }

    private fun writeAirQuality(result: AirQualityResult?) {
        if (result == null) {
            weatherStore.set(airQuality = null)
            return
        }
        when (result) {
            is AirQualityResult.Success -> {
                weatherStore.set(airQuality = result.airquality)
            }
            // Fail silently, we just won't show the Air quality in the UI if null
            is AirQualityResult.Error -> {
                weatherStore.set(airQuality = result.cacheAirQuality)
            }
        }
    }


    private fun writeAlerts(result: AlertResult?) {
        if (result == null) {
            weatherStore.set(alerts = emptyList())
            return
        }
        when (result) {
            is AlertResult.Success -> {
                weatherStore.set(alerts = result.alerts)
            }
            // Fail silently, we just won't show the alerts in the UI if null
            is AlertResult.Error -> {
                weatherStore.set(alerts = result.cacheAlerts)
            }
        }
    }

}