package com.pranshulgg.weather_master_app.core.managers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.sources.isGlobal
import com.pranshulgg.weather_master_app.core.model.sources.isSourceSupportedFor
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityResult
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResult
import com.pranshulgg.weather_master_app.data.repository.WeatherContextRepository
import com.pranshulgg.weather_master_app.data.repository.data.SourceDataRepository
import com.pranshulgg.weather_master_app.data.store.InitializationStore
import com.pranshulgg.weather_master_app.data.store.LocationStore
import com.pranshulgg.weather_master_app.data.store.WeatherStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.cancel
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class WeatherManager @Inject constructor(
    private val weatherContextRepository: WeatherContextRepository,
    private val sourceDataRepository: SourceDataRepository,
    private val weatherStore: WeatherStore,
    private val locationStore: LocationStore,
    private val initializationStore: InitializationStore
) {

    private val scope = CoroutineScope(SupervisorJob())

    private val _errors = MutableSharedFlow<AppException>(
        extraBufferCapacity = 1
    )

    var loading by mutableStateOf(false)
        private set

    /**
     * Lets the UI know that the source for this location
     * isn't supported in the region
     */
    var isUnsupportedSource by mutableStateOf(false)
        private set

    val errors = _errors.asSharedFlow()

    private var weatherJob: Job? = null


    operator fun invoke(
        location: Location,
        isManualRefresh: Boolean = false,
        isForceRefresh: Boolean = false,
        isForceRefreshForAirQuality: Boolean = false,
        isForceRefreshForAlerts: Boolean = false,
        skipDeviceLocationCheck: Boolean = false,
    ) {

        loading = true
        isUnsupportedSource = false
        val startTime = System.currentTimeMillis()
        weatherJob?.cancel()

        weatherJob = scope.launch {

            var effectiveLocation = location
            var effectiveForceRefresh = isForceRefresh
            var effectiveForceRefreshForAirQuality = isForceRefreshForAirQuality
            var effectiveForceRefreshForAlerts = isForceRefreshForAlerts

            if (location.isDeviceLocation && !skipDeviceLocationCheck) {
                val positionChanged = weatherContextRepository.updateDeviceLocationPosition()
                if (positionChanged) {
                    effectiveLocation = weatherContextRepository.getLocationForId(location.id)
                    effectiveForceRefresh = true
                    effectiveForceRefreshForAirQuality = true
                    effectiveForceRefreshForAlerts = true
                    locationStore.setActiveLocation(effectiveLocation)
                }
            }


            sourceDataRepository.getData(
                location = effectiveLocation,
                isManualRefresh = isManualRefresh,
                isForceRefresh = effectiveForceRefresh,
                isForceRefreshForAirQuality = effectiveForceRefreshForAirQuality,
                isForceRefreshForAlerts = effectiveForceRefreshForAlerts,
                onWeather = { result ->
                    writeWeather(result, effectiveLocation)
                },
                onAlerts = { result ->
                    writeAlerts(result)
                },
                onAirQuality = { result ->
                    writeAirQuality(result)
                },
            )

            val elapsed = System.currentTimeMillis() - startTime
            val minLoadingTime = 1000L

            // Prevents loader flicker when responses return too quickly
            if (elapsed < minLoadingTime) {
                delay(duration = (minLoadingTime - elapsed).milliseconds)
            }

            loading = false
        }
    }

    private fun writeWeather(
        result: WeatherResult,
        location: Location
    ) {
        when (result) {

            is WeatherResult.Success -> {
                weatherStore.set(weather = result.weather)
                initializationStore.setInitialized()
            }

            is WeatherResult.Error -> {
                weatherStore.set(weather = result.cacheWeather)

                isUnsupportedSource = !location.source.isGlobal()
                        && !location.source.isSourceSupportedFor(
                    countryCode = location.countryCode?.uppercase()
                )

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