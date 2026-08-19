package com.pranshulgg.weather_master_app.core.network.sources.weather.jma.json

// Top-level response is Map<amedasId, JmaAmedasStationJson>.
// lat/lon are [degrees, minutes] pairs, not decimal degrees - convert via deg + min/60.
data class JmaAmedasStationJson(
    val lat: List<Double>? = null,
    val lon: List<Double>? = null,
    val enName: String? = null
)
