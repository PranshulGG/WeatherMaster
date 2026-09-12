package com.pranshulgg.weather_master_app.core.managers.requests

import android.util.Log
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class WeatherRefreshRequestState(
    val location: Location,
    val isManualRefresh: Boolean,
    val forceRefresh: Boolean,
    val forceRefreshForAirQuality: Boolean,
    val forceRefreshForAlerts: Boolean,
)

@Singleton
class PendingRequests @Inject constructor() {

    private val _pendingRequest =
        MutableStateFlow<WeatherRefreshRequestState?>(null)

    val pendingRequest = _pendingRequest.asStateFlow()

    fun queueRequest(
        location: Location,
        isManualRefresh: Boolean = false,
        isForceRefresh: Boolean = false,
        isForceRefreshForAirQuality: Boolean = false,
        isForceRefreshForAlerts: Boolean = false
    ) {
        Log.d(
            "PendingRequests",
            "queueRequest() called: location=${location.id}, " +
                    "force=$isForceRefresh, " +
                    "forceAQ=$isForceRefreshForAirQuality, " +
                    "forceAlerts=$isForceRefreshForAlerts"
        )
        _pendingRequest.tryEmit(
            WeatherRefreshRequestState(
                location = location,
                forceRefresh = isForceRefresh,
                forceRefreshForAirQuality = isForceRefreshForAirQuality,
                forceRefreshForAlerts = isForceRefreshForAlerts,
                isManualRefresh = isManualRefresh
            )
        )
        Log.d(
            "PendingRequests",
            "State updated: ${_pendingRequest.value}"
        )
    }
}