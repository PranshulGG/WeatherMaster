package com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json.onecall

import com.google.gson.annotations.SerializedName

data class OpenWeatherOneCallHourlyJson(
    val data: List<OpenWeatherOneCallHourlyItemJson>
)

data class OpenWeatherOneCallHourlyItemJson(
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
    val pop: Double?,
    val rain: OpenWeatherOneCallPrecipJson?,
    val snow: OpenWeatherOneCallPrecipJson?
)
