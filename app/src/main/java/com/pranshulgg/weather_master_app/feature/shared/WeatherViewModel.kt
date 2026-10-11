package com.pranshulgg.weather_master_app.feature.shared

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.core.managers.ExternalManager
import com.pranshulgg.weather_master_app.core.managers.LocationManager
import com.pranshulgg.weather_master_app.core.managers.SourceManager
import com.pranshulgg.weather_master_app.core.managers.WeatherBlocksManager
import com.pranshulgg.weather_master_app.core.managers.WeatherManager
import com.pranshulgg.weather_master_app.core.managers.WeatherUnitsManager
import com.pranshulgg.weather_master_app.core.managers.requests.AutoRefreshes
import com.pranshulgg.weather_master_app.core.managers.requests.PendingRequests
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.openmeteo.OpenMeteoModel
import com.pranshulgg.weather_master_app.core.network.sources.weather.rain.RainNowcastRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherContextRepository
import com.pranshulgg.weather_master_app.data.store.LocationStore
import com.pranshulgg.weather_master_app.feature.main.MainScreenWeatherUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

// DO NOT USE STORES HERE!!

/**
 * TODO: update widgets/notification
 */
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherManager: WeatherManager,
    private val locationManager: LocationManager,
    private val weatherUnitsManager: WeatherUnitsManager,
    private val weatherBlocksManager: WeatherBlocksManager,
    private val pendingRequests: PendingRequests,
    private val autoRefreshes: AutoRefreshes,
    private val rainNowcastRepository: RainNowcastRepository
) : ViewModel() {

    private var _uiState = mutableStateOf(MainScreenWeatherUiState())
    val uiState: State<MainScreenWeatherUiState> = _uiState
    private var rainNowcastJob: Job? = null
    val errors = weatherManager.errors
    val isUnSupportedSource = weatherManager.isUnsupportedSource


    init {
        viewModelScope.launch {
            locationManager.initialize(viewModelScope)
            weatherBlocksManager.initialize()
        }
        weatherUnitsManager.initialize(viewModelScope)
        autoRefreshes.start()

        /**
         * Observe source changes here.
         * Manager should emit an event whenever any source changes
         */
        pendingRequests.pendingRequest.onEach { req ->
            if (req != null) {
                getWeather(
                    location = req.location,
                    isForceRefresh = req.forceRefresh,
                    isForceRefreshForAirQuality = req.forceRefreshForAirQuality,
                    isForceRefreshForAlerts = req.forceRefreshForAlerts,
                    isManualRefresh = req.isManualRefresh
                )
            }
        }.launchIn(viewModelScope)
    }


    fun getWeather(
        location: Location,
        isManualRefresh: Boolean = false,
        isForceRefresh: Boolean = false,
        isForceRefreshForAirQuality: Boolean = false,
        isForceRefreshForAlerts: Boolean = false
    ) {

        // high-res rain nowcast for the "Highlight rain" option
        loadRainNowcast(location)

        weatherManager(
            location,
            isManualRefresh,
            isForceRefresh,
            isForceRefreshForAirQuality,
            isForceRefreshForAlerts
        )
    }


    /**
     * Fetches the 2 hour rain nowcast (KNMI radar, DWD radar or the keyless
     * Open-Meteo minutely-15 fallback) for the given location and publishes it
     * to the ui state. Independent of the weather source, so the block simply
     * hides itself when there is no nowcast for the location.
     */
    private fun loadRainNowcast(location: Location) {
        rainNowcastJob?.cancel()
        rainNowcastJob = viewModelScope.launch {
            val nowcast = try {
                rainNowcastRepository.getNowcast(location)
            } catch (e: Exception) {
                null
            }
            _uiState.value = _uiState.value.copy(rainNowcast = nowcast)
        }
    }

    fun setActiveLocation(location: Location, skipLoading: Boolean) {
        locationManager.setActive(location, skipLoading)
    }

    fun setActiveLoading() {
        locationManager.setActiveLoading()
    }

    fun refreshWeather(location: Location?) {
        location?.let {
            getWeather(location, isManualRefresh = true)
        }

    }


}
