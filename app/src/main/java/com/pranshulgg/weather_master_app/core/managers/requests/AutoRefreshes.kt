package com.pranshulgg.weather_master_app.core.managers.requests

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.core.managers.LocationManager
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.data.store.LocationStore
import com.pranshulgg.weather_master_app.data.store.WeatherStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.minutes

@Singleton
class AutoRefreshes @Inject constructor(
    private val locationStore: LocationStore,
    private val weatherStore: WeatherStore,
    private val pendingRequests: PendingRequests,
    private val locationManager: LocationManager
) {
    private var autoRefreshJob: Job? = null

    private val scope = CoroutineScope(SupervisorJob())


    fun start() {

        if (autoRefreshJob?.isActive == true) return

        autoRefreshJob = scope.launch {
            while (isActive) {
                delay(45.minutes)
                if (locationStore.data.value.isActiveLocationLoading || weatherStore.data.value.weather == null) {
                    continue
                }

                locationStore.data.value.activeLocation?.let {
                    locationManager.setActiveLoading()
                    pendingRequests.queueRequest(location = it, skipDeviceLocationCheck = true)
                }
            }
        }
    }

    fun stop() {
        autoRefreshJob?.cancel()
        autoRefreshJob = null
    }

}