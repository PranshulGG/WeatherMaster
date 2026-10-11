package com.pranshulgg.weather_master_app.data.store

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton


data class LocationStoreState(
    val locations: List<Location> = emptyList(),
    val activeLocation: Location? = null,
    val isActiveLocationLoading: Boolean = false
)

@Singleton
class LocationStore @Inject constructor() {

    private val _data = MutableStateFlow(LocationStoreState())
    val data = _data.asStateFlow()


    fun setLocations(locations: List<Location>) {
        _data.update {
            it.copy(locations = locations)
        }
    }

    fun setActiveLocation(activeLocation: Location?) {
        _data.update {
            it.copy(activeLocation = activeLocation)
        }
    }

    fun setLoading(value: Boolean) {
        _data.update {
            it.copy(isActiveLocationLoading = value)
        }
    }

    fun clear() {
        _data.update {
            it.copy(
                activeLocation = null,
                locations = emptyList(),
            )
        }
    }
}