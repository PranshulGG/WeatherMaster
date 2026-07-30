package com.pranshulgg.weather_master_app.core.network.sources.alerts.weatherapi.json


data class AlertsWeatherApiJson(
    val alerts: AlertsWeatherApiAlertJson
)

data class AlertsWeatherApiAlertJson(
    val alert: List<AlertsWeatherApiAlertItemJson>
)

data class AlertsWeatherApiAlertItemJson(
    val event: String,
    val effective: String?,
    val expires: String?,
    val desc: String?,
    val severity: String?,
    val headline: String?
)