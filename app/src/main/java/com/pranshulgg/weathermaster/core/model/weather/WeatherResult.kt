package com.pranshulgg.weathermaster.core.model.weather

import com.pranshulgg.weathermaster.core.model.domain.weather.Weather

sealed class WeatherResult {
    data class Success(val weather: Weather) : WeatherResult()
    data object RefreshNotAvailable : WeatherResult()

    data class Error(val exception: Exception, val cacheWeather: Weather? = null) : WeatherResult()
}

enum class WeatherResultType {
    REFRESH_TOO_EARLY,
    SUCCESS,
    ERROR
}