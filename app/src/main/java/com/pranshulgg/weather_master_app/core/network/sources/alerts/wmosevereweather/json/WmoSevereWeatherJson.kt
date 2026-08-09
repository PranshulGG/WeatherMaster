package com.pranshulgg.weather_master_app.core.network.sources.alerts.wmosevereweather.json

import com.google.gson.annotations.SerializedName

data class WmoSevereWeatherJson(
    val features: List<WmoSevereWeatherFeatureJson>
)

data class WmoSevereWeatherFeatureJson(
    val properties: WmoSevereWeatherFeaturePropertiesJson?
)


data class WmoSevereWeatherFeaturePropertiesJson(
    @SerializedName("capurl") val capUrl: String?,
    @SerializedName("rlink") val rLink: String?,
    val description: String?,
    val event: String?,
    val effective: String?,
    val expires: String?,
    @SerializedName("s") val severity: Int?,
    val headline: String?
)