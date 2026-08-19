package com.pranshulgg.weather_master_app.data.repository.data

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult

interface WeatherRepository {
    val weatherSource: Source

    // Keep them true if air quality or alerts are provided along with the weather response
    // rather than a separate API endpoint
    val providesAlerts: Boolean
        get() = false

    val providesAirQuality: Boolean
        get() = false

    suspend fun getWeather(
        location: Location,
        isManualRefresh: Boolean = false,
        isForceRefresh: Boolean
    ): WeatherResult

}