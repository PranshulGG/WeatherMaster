package com.pranshulgg.weather_master_app.core.network.sources.weather.openmeteo.json

import com.google.gson.annotations.SerializedName

/**
 * Minimal response model for Open-Meteo's minutely_15 precipitation data,
 * used as the keyless fallback for the high-resolution rain nowcast in
 * regions without KNMI/DWD radar coverage (e.g. India).
 *
 * Requested with timeformat=unixtime, so timestamps are epoch seconds.
 */
data class OpenMeteoMinutely15Json(
    val latitude: Double?,
    val longitude: Double?,
    @SerializedName("utc_offset_seconds") val utcOffsetSeconds: Long?,
    @SerializedName("minutely_15") val minutely15: OpenMeteoMinutely15Block?
)

data class OpenMeteoMinutely15Block(
    val time: List<Long>?,
    val precipitation: List<Double?>?
)