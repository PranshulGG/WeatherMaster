package com.pranshulgg.weather_master_app.core.network.sources.weather.nws.json

data class NwsAlertsJson(
    val features: List<NwsAlertFeatureJson>?
)

data class NwsAlertFeatureJson(
    val properties: NwsAlertPropertiesJson?
)

data class NwsAlertPropertiesJson(
    val id: String?,
    val event: String?,
    val severity: String?,
    val effective: String?,
    val expires: String?,
    val description: String?,
    val senderName: String?
)
