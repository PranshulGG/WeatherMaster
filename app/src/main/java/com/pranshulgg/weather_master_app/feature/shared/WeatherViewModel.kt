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
import com.pranshulgg.weather_master_app.core.managers.requests.PendingRequests
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.openmeteo.OpenMeteoModel
import com.pranshulgg.weather_master_app.data.repository.WeatherContextRepository
import com.pranshulgg.weather_master_app.data.store.LocationStore
import com.pranshulgg.weather_master_app.feature.main.MainScreenWeatherUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

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
    private val pendingRequests: PendingRequests
) : ViewModel() {

    private var _uiState = mutableStateOf(MainScreenWeatherUiState())
    val uiState: State<MainScreenWeatherUiState> = _uiState
    val errors = weatherManager.errors
    val isUnSupportedSource = weatherManager.isUnsupportedSource


    init {
        viewModelScope.launch {
            locationManager.initialize(viewModelScope)
            weatherBlocksManager.initialize()
        }
        weatherUnitsManager.initialize(viewModelScope)


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

        weatherManager(
            location,
            isManualRefresh,
            isForceRefresh,
            isForceRefreshForAirQuality,
            isForceRefreshForAlerts
        )
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
