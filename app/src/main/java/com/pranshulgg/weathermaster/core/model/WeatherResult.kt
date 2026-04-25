package com.pranshulgg.weathermaster.core.model

import com.pranshulgg.weathermaster.core.model.domain.Weather

sealed class WeatherResult {

    data class Success(val weather: Weather) : WeatherResult()

    data object RefreshNotAvailable : WeatherResult()

    data class Error(val message: String, val cacheWeather: Weather? = null) : WeatherResult()
}

enum class WeatherResultType {
    REFRESH_TOO_EARLY,
    SUCCESS,
    ERROR
}