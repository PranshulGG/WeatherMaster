package com.pranshulgg.weather_master_app.core.model.weather.airquality

import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.data.repository.data.AlertsAdditionalData


sealed class AirQualityResult {
    data class Success(val airQuality: AirQuality) : AirQualityResult()
    data class Error(val exception: Exception, val cacheAirQuality: AirQuality? = null) :
        AirQualityResult()
}

data class FinishedAirQualityResult(
    val airQuality: AirQuality
)

data class AirQualityDataPack(
    val airQuality: AirQuality,
    val location: Location,
    val additionalData: AlertsAdditionalData? = null,
)


enum class AirQualityResultType {
    RETURN_CACHE,
    ERROR
}