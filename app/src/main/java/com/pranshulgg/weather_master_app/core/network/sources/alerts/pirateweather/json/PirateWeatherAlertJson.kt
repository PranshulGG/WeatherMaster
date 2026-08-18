package com.pranshulgg.weather_master_app.core.network.sources.alerts.pirateweather.json

data class PirateWeatherAlertJson(
    val title: String,
    val regions: List<String>?,
    val severity: String,
    val time: Long,
    val expires: Long,
    val description: String,
    val uri: String?
)
