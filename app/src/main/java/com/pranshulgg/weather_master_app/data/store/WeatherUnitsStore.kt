package com.pranshulgg.weather_master_app.data.store

import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

data class WeatherUnitsStoreState(
    val units: WeatherUnits = WeatherUnits.getDefault()
)

@Singleton
class WeatherUnitsStore @Inject constructor() {
    private val _data = MutableStateFlow(WeatherUnitsStoreState())
    val data = _data.asStateFlow()

    fun set(units: WeatherUnits) {
        _data.update {
            it.copy(
                units = units
            )
        }
    }

}