package com.pranshulgg.weather_master_app.data.store

import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class WeatherStoreState(
    val weather: Weather? = null,
    val airQuality: AirQuality? = null,
    val alerts: List<Alert> = emptyList(),
)

class WeatherStore @Inject constructor() {

    private val _data = MutableStateFlow(WeatherStoreState())
    val data = _data.asStateFlow()


    fun set(
        weather: Weather? = null,
        airQuality: AirQuality? = null,
        alerts: List<Alert> = emptyList()
    ) {

        _data.update {
            it.copy(
                weather = weather,
                airQuality = airQuality,
                alerts = alerts
            )
        }
    }

    fun clear() {
        _data.update {
            it.copy(
                weather = null,
                airQuality = null,
                alerts = emptyList()
            )
        }
    }
}