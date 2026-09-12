package com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json.onecall

import com.google.gson.annotations.SerializedName

data class OpenWeatherOneCallCurrentJson(
    val data: List<OpenWeatherOneCallCurrentItemJson>
)

data class OpenWeatherOneCallCurrentItemJson(
    val dt: Long,
    val temp: Double?,
    @SerializedName("feels_like") val feelsLike: Double?,
    val pressure: Double?,
    val humidity: Double?,
    @SerializedName("dew_point") val dewPoint: Double?,
    val uvi: Double?,
    val clouds: Double?,
    val visibility: Double?,
    @SerializedName("wind_speed") val windSpeedMs: Double?,
    @SerializedName("wind_deg") val windDeg: Double?,
    val weather: List<OpenWeatherOneCallWeatherJson>,
    val rain: OpenWeatherOneCallPrecipJson?,
    val snow: OpenWeatherOneCallPrecipJson?
)
