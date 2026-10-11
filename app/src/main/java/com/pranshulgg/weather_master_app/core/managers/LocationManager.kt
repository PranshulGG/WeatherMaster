package com.pranshulgg.weather_master_app.core.managers

import android.util.Log
import com.pranshulgg.weather_master_app.core.managers.requests.PendingRequests
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.openmeteo.OpenMeteoModel
import com.pranshulgg.weather_master_app.data.repository.WeatherContextRepository
import com.pranshulgg.weather_master_app.data.store.InitializationStore
import com.pranshulgg.weather_master_app.data.store.LocationStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationManager @Inject constructor(
    private val weatherContextRepository: WeatherContextRepository,
    private val locationStore: LocationStore,
    private val initializationStore: InitializationStore,
    private val pendingRequests: PendingRequests

) {

    suspend fun initialize(scope: CoroutineScope) {
        if (!initializationStore.data.value.isInitialized && locationStore.data.value.activeLocation == null) {
            if (weatherContextRepository.isLocationsEmpty()) {
                initializationStore.setInitialized()
            }
            val default = weatherContextRepository
                .getDefaultLocation()
                .filterNotNull()
                .first()

            setActive(default, skipLoading = false)
        }

        weatherContextRepository.getLocations().distinctUntilChanged()
            .onEach { locations ->
                writeLocations(
                    locations = locations,
                    isLoading = locationStore.data.value.isActiveLocationLoading
                )
            }.launchIn(scope)
    }

    private fun writeLocations(locations: List<Location>, isLoading: Boolean) {

        val previous = locationStore.data.value.locations

        if (previous.isNotEmpty()) {

            val newLocation = locations.firstOrNull { new ->
                previous.none { it.id == new.id }
            }

            newLocation?.let {
                if (!isLoading) {
                    setActive(newLocation, skipLoading = false)
                }
            }
        }

        locationStore.setLocations(locations = locations)
    }

    suspend fun deleteLocation(id: String) {
        weatherContextRepository.deleteLocation(id)

        if (locationStore.data.value.activeLocation?.id == id) {
            val defaultLocation = locationStore.data.value.locations
                .first { it.isDefault }

            setActive(defaultLocation, skipLoading = false)
        }
    }

    fun setActive(location: Location, skipLoading: Boolean) {
        if (!skipLoading) {
            setActiveLoading()
        }
        locationStore.setActiveLocation(location)
        pendingRequests.queueRequest(location)
    }

    fun setActiveLoading() {
        locationStore.setLoading(true)
    }
}
