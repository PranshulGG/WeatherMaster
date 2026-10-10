package com.pranshulgg.weather_master_app.core.network.sources.airquality.moenv.json

import com.google.gson.annotations.SerializedName


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