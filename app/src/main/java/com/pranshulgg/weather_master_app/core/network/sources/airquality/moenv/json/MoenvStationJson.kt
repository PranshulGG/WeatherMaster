package com.pranshulgg.weather_master_app.core.network.sources.airquality.moenv.json

import com.google.gson.annotations.SerializedName

// All fields come back as strings, including numeric ones, and some (e.g. "o3") can be an
// empty string when a station is offline/calibrating - confirmed live. Parsed with
// toSafeDouble() rather than typed as Double so an empty string doesn't fail deserialization.
data class MoenvStationJson(
    val sitename: String?,
    val county: String?,
    val aqi: String?,
    val status: String?,
    val latitude: String?,
    val longitude: String?,
    val so2: String?,
    val co: String?,
    val o3: String?,
    val pm10: String?,
    @SerializedName("pm2.5") val pm25: String?,
    val no2: String?,
)
