package com.pranshulgg.weather_master_app.data.repository

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityResult


interface AirQualityRepository {
    suspend fun getAirQuality(
        location: Location,
        isManualRefresh: Boolean = false,
        isForceRefresh: Boolean
    ): AirQualityResult

}