package com.pranshulgg.weather_master_app.core.network.sources.weather.aemet.model

// Every AEMET OpenData endpoint returns this small envelope first;
// the actual payload is a separate, short-lived, unauthenticated fetch of `datos`.
data class AemetEnvelopeJson(
    val descripcion: String?,
    val estado: Int?,
    val datos: String?,
    val metadatos: String?
)
