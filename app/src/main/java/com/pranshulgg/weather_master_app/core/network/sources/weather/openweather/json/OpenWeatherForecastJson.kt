package com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json

import com.google.gson.annotations.SerializedName


data class OpenWeatherForecastJson(
    val list: List<OpenWeatherForecastItemJson>
)

data class OpenWeatherForecastItemJson(
    val dt: Long,
    val main: OpenWeatherForecastItemMainJson,
    val weather: List<OpenWeatherForecastItemWeatherJson>,
    val wind: OpenWeatherForecastItemWindJson,
    val visibility: Double?,
    val pop: Double?,
    val rain: OpenWeatherForecastItemRainJson?,
    val snow: OpenWeatherForecastItemSnowJson?

)

data class OpenWeatherForecastItemMainJson(
    val temp: Double?,
    @SerializedName("feels_like") val feelsLike: Double?,
    @SerializedName("sea_level") val pressureSeaLevel: Double?,
    val humidity: Double?,
    @SerializedName("dew_point") val dewPoint: Double?,

    )

data class OpenWeatherForecastItemWeatherJson(
    val icon: String?
)

data class OpenWeatherForecastItemWindJson(
    @SerializedName("speed") val speedMs: Double?,
    val deg: Double?
)

data class OpenWeatherForecastItemRainJson(
    @SerializedName("3h") val amountMm: Double?
)

data class OpenWeatherForecastItemSnowJson(
    @SerializedName("3h") val amountMm: Double?
)