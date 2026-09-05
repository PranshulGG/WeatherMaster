package com.pranshulgg.weather_master_app.core.model.weather

import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.data.repository.weather.WeatherAdditionalData

sealed class WeatherResult(
) {

    data class Success(
        val weather: Weather, val alerts: List<Alert> = emptyList()
    ) : WeatherResult()

    data class RefreshNotAvailable(val weather: Weather) : WeatherResult()

    data class Error(val exception: Exception, val weather: Weather?) : WeatherResult()

}

data class WeatherDataPack(
    val weather: Weather,
    val additionalData: WeatherAdditionalData? = null
)

data class FinishedWeatherResult(
    val weather: Weather
)

enum class WeatherResultType {
    REFRESH_TOO_EARLY,
    SUCCESS,
    ERROR
}