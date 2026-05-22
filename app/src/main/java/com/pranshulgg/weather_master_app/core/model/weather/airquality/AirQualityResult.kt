package com.pranshulgg.weather_master_app.core.model.weather.airquality

import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality


sealed class AirQualityResult {
    data class Success(val airquality: AirQuality) : AirQualityResult()
    data class Error(val exception: Exception, val cacheAirQuality: AirQuality? = null) :
        AirQualityResult()
}

enum class AirQualityResultType {
    RETURN_CACHE,
    ERROR
}