package com.pranshulgg.weather_master_app.data.repository

import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.domain.location.Location

interface WeatherRepository {
    suspend fun getWeather(
        location: Location,
        isManualRefresh: Boolean = false,
        isForceRefresh: Boolean
    ): WeatherResult

}