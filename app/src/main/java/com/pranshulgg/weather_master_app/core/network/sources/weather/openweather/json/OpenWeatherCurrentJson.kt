package com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json

import com.google.gson.annotations.SerializedName

data class OpenWeatherCurrentJson(
    val dt: Long,
    val main: OpenWeatherCurrentItemMainJson,
    val weather: List<OpenWeatherCurrentItemWeatherJson>,
    val wind: OpenWeatherCurrentItemWindJson,
    val visibility: Double?,
    val pop: Double?,
    val rain: OpenWeatherCurrentItemRainJson?,
    val snow: OpenWeatherCurrentItemSnowJson?

)


data class OpenWeatherCurrentItemMainJson(
    val temp: Double?,
    @SerializedName("feels_like") val feelsLike: Double?,
    @SerializedName("sea_level") val pressureSeaLevel: Double?,
    val humidity: Double?,
    @SerializedName("dew_point") val dewPoint: Double?,

    )

data class OpenWeatherCurrentItemWeatherJson(
    val icon: String?
)

data class OpenWeatherCurrentItemWindJson(
    @SerializedName("speed") val speedMs: Double?,
    val deg: Double?
)

data class OpenWeatherCurrentItemRainJson(
    @SerializedName("1h") val amountMm: Double?
)

data class OpenWeatherCurrentItemSnowJson(
    @SerializedName("1h") val amountMm: Double?
)