package com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo.json

import com.google.gson.annotations.SerializedName

/**
 * Minimal response model for Open-Meteo's minutely_15 precipitation data,
 * used as fallback / tail extension for the high-resolution rain nowcast.
 */
data class OpenMeteoMinutely15Json(
    val latitude: Double?,
    val longitude: Double?,
    @SerializedName("utc_offset_seconds") val utcOffsetSeconds: Long?,
    @SerializedName("minutely_15") val minutely15: OpenMeteoMinutely15Block?
)

data class OpenMeteoMinutely15Block(
    val time: List<String>?,
    val precipitation: List<Double?>?
)