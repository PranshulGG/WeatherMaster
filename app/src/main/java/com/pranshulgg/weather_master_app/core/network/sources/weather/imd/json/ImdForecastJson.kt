package com.pranshulgg.weather_master_app.core.network.sources.weather.imd.json

import com.google.gson.annotations.SerializedName

data class ImdForecastJson(
    @SerializedName("rh") val humidity: List<String?>,
    @SerializedName("temp") val temperature: List<String?>,
    @SerializedName("wdir") val windDirection: List<String?>,
    @SerializedName("wspd") val windSpeedMs: List<String?>,
    @SerializedName("apcp") val precipitation: List<String?>,
    @SerializedName("tcdc") val cloudCover: List<String?>,

    )