package com.pranshulgg.weather_master_app.core.network.sources.weather.dwd.json

import com.google.gson.annotations.SerializedName


data class DwdCurrentWeatherJson(
    val weather: DwdCurrentWeatherDataJson
)


data class DwdWeatherForecastJson(
    val weather: List<DwdWeatherForecastDataJson>
)

data class DwdWeatherForecastDataJson(
    val timestamp: String,

    @SerializedName("dew_point") val dewPoint: Double?,
    @SerializedName("pressure_msl") val pressureMsl: Double?,
    @SerializedName("relative_humidity") val humidity: Int?,
    val visibility: Int?,
    @SerializedName("wind_direction") val windDirection: Int?,
    @SerializedName("wind_speed") val windSpeed: Double?,
    val temperature: Double,
    val icon: String,
    val precipitation: Double?,
    @SerializedName("precipitation_probability") val precipitationProbability: Int?,
)

data class DwdCurrentWeatherDataJson(
    val timestamp: String,

    @SerializedName("dew_point") val dewPoint: Double?,
    @SerializedName("pressure_msl") val pressureMsl: Double?,
    @SerializedName("relative_humidity") val humidity: Int?,
    val visibility: Int?,
    @SerializedName("wind_direction_10") val windDirection: Int?,
    @SerializedName("wind_speed_10") val windSpeed: Double?,
    val temperature: Double,
    val icon: String
)