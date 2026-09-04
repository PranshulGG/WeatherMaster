package com.pranshulgg.weather_master_app.core.model.weather

import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather

sealed class WeatherResult(
) {

    data class Success(
        val weather: Weather, val alerts: List<Alert> = emptyList()
    ) : WeatherResult()

    data class RefreshNotAvailable(val weather: Weather) : WeatherResult()

    data class Error(val exception: Exception, val weather: Weather?) : WeatherResult()

}

enum class WeatherResultType {
    REFRESH_TOO_EARLY,
    SUCCESS,
    ERROR
}