package com.pranshulgg.weathermaster.core.model.domain

// TODO: ADD HOURLY-DAILY FORECAST

/**
 * Keeping air quality separate from the main weather domain
 * We don't wanna delay weather from displaying because the API hasn't returned anything or is slow
 * We only display air quality if it's available
 */
data class AirQuality(
    val current: AirQualityCurrent
)

data class AirQualityCurrent(
    val usAqi: Int?,
    val pm10: Double?,
    val pm25: Double?,
    val carbonMonoxide: Double?,
    val nitrogenDioxide: Double?,
    val sulphurDioxide: Double?,
    val ozone: Double?,
    val lastUpdatedSecs: Long
)