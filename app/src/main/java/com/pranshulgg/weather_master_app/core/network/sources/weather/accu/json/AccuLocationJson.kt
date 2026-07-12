package com.pranshulgg.weather_master_app.core.network.sources.weather.accu.json

import com.google.gson.annotations.SerializedName

data class AccuLocationJson(
    @SerializedName("Key") val key: String
)
