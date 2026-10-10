package com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.json

data class MgmAlertJson(
    val text: MgmAlertTextJson?,
    val weather: MgmAlertWeatherJson?,
    val towns: MgmAlertTownsJson?,
    val begin: String?,
    val end: String?,
)

data class MgmAlertTextJson(
    val yellow: String?,
    val orange: String?,
    val red: String?
)

data class MgmAlertWeatherJson(
    val yellow: List<String?>,
    val orange: List<String?>,
    val red: List<String?>
)

data class MgmAlertTownsJson(
    val yellow: List<Long?>,
    val orange: List<Long?>,
    val red: List<Long?>
)