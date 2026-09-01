package com.pranshulgg.weather_master_app.data.store

import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import com.pranshulgg.weather_master_app.data.repository.data.SourceDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class WeatherStoreState(
    val weather: Weather? = null,
    val weatherUnits: WeatherUnits = WeatherUnits.getDefault(),
    val airQuality: AirQuality? = null,
    val alerts: List<Alert> = emptyList(),
    val isWeatherLoaded: Boolean = false
)

class WeatherStore @Inject constructor() {

    private val _data = MutableStateFlow(WeatherStoreState())
    val data = _data.asStateFlow()

    fun set(
        weather: Weather? = null,
        airQuality: AirQuality? = null,
        alerts: List<Alert> = emptyList(),
        isWeatherLoaded: Boolean = false
    ) {

        _data.update {
            it.copy(
                weather = weather,
                airQuality = airQuality,
                alerts = alerts,
                isWeatherLoaded = isWeatherLoaded
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