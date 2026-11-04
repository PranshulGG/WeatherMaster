package com.pranshulgg.weather_master_app.core.network.sources.alerts.metservicenz.model

data class MetserviceNzCapAlert(
    val event: String?,
    val severity: String?,
    val onset: String?,
    val expires: String?,
    val senderName: String?,
    val headline: String?,
    val description: String?,
    val polygons: List<List<Pair<Double, Double>>>
)
