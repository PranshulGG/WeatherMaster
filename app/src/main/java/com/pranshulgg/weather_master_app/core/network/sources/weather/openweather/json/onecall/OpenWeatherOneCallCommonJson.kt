package com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json.onecall

import com.google.gson.annotations.SerializedName

data class OpenWeatherOneCallWeatherJson(
    val icon: String?
)

data class OpenWeatherOneCallPrecipJson(
    @SerializedName("1h") val amountMm: Double?
)
