package com.pranshulgg.weather_master_app.data.store

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


data class LocationStoreState(
    val locations: List<Location> = emptyList(),
    val activeLocation: Location? = null
)

class LocationStore @Inject constructor() {

    private val _data = MutableStateFlow(LocationStoreState())
    val data = _data.asStateFlow()

    fun set(
        activeLocation: Location?,
        locations: List<Location> = emptyList()
    ) {

        _data.update {
            it.copy(
                activeLocation = activeLocation,
                locations = locations,
            )
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