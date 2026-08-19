package com.pranshulgg.weather_master_app.data.repository.data

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityResult

interface AirQualityRepository {

    val airQualitySource: Source
    suspend fun getAirQuality(
        location: Location,
        isManualRefresh: Boolean = false,
        isForceRefresh: Boolean = false,
    ): AirQualityResult

}