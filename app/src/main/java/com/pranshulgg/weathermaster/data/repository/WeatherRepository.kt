package com.pranshulgg.weathermaster.data.repository

import com.pranshulgg.weathermaster.core.model.weather.WeatherResult
import com.pranshulgg.weathermaster.core.model.domain.location.Location

interface WeatherRepository {
    suspend fun getWeather(
        location: Location,
        isManualRefresh: Boolean = false,
        isForceRefresh: Boolean
    ): WeatherResult

}