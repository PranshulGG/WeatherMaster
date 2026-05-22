package com.pranshulgg.weather_master_app.core.model.domain.weather.nws


data class NwsGridPoints(
    val locationId: String,
    val officeId: String,
    val gridX: Long,
    val gridY: Long,
    val stationIdentifier: String?,
    val lastUpdatedMilli: Long
)