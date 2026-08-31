package com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json.onecall

import com.google.gson.annotations.SerializedName

data class OpenWeatherOneCallDailyJson(
    val data: List<OpenWeatherOneCallDailyItemJson>
)

data class OpenWeatherOneCallDailyItemJson(
    val dt: Long,
    val temp: OpenWeatherOneCallDailyTempJson,
    val pressure: Double?,
    val humidity: Double?,
    @SerializedName("dew_point") val dewPoint: Double?,
    @SerializedName("wind_speed") val windSpeedMs: Double?,
    @SerializedName("wind_deg") val windDeg: Double?,
    val weather: List<OpenWeatherOneCallWeatherJson>,
    val clouds: Double?,
    val pop: Double?,
    val rain: Double?,
    val snow: Double?,
    val uvi: Double?
)

data class OpenWeatherOneCallDailyTempJson(
    val day: Double?,
    val min: Double?,
    val max: Double?,
    val night: Double?,
    val eve: Double?,
    val morn: Double?
)
