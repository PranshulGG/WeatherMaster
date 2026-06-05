package com.pranshulgg.weather_master_app.core.network.sources.weather.meteofrance.json

import com.google.gson.annotations.SerializedName

data class MeteoFranceForecastJson(
    val properties: MeteoFranceForecastPropertiesJson
)

data class MeteoFranceForecastPropertiesJson(
    val forecast: List<MeteoFranceForecastItemJson>,
    @SerializedName("daily_forecast") val daily: List<MeteoFranceDailyForecastItemJson>
)

data class MeteoFranceForecastItemJson(
    val time: Long,
    @SerializedName("T") val temperature: Double?,
    @SerializedName("relative_humidity") val humidity: Int?,
    @SerializedName("P_sea") val pressureMsl: Double?,
    @SerializedName("wind_speed") val windSpeed: Int?,
    @SerializedName("wind_direction") val windDirection: Int?,
    @SerializedName("rain_1h") val rain: Double?,
    @SerializedName("snow_1h") val snow: Double?,
    @SerializedName("weather_icon") val icon: String
)

data class MeteoFranceDailyForecastItemJson(
    val time: Long,
    @SerializedName("T_min") val temperatureMin: Double?,
    @SerializedName("T_max") val temperatureMax: Double?,
    @SerializedName("relative_humidity_max") val humidity: Int?,
    @SerializedName("weather_icon") val icon: String,
    @SerializedName("uv_index") val uvIndex: Double?,

    )