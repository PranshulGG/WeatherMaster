package com.pranshulgg.weather_master_app.core.network.sources.weather.imd.json

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class ImdForecastJson(
    @SerializedName("rh") val humidity: List<JsonElement?>,
    @SerializedName("temp") val temperature: List<JsonElement?>,
    @SerializedName("wdir") val windDirection: List<JsonElement?>,
    @SerializedName("wspd") val windSpeedMs: List<JsonElement?>,
    @SerializedName("apcp") val precipitation: List<JsonElement?>,
    @SerializedName("tcdc") val cloudCover: List<JsonElement?>,

    )