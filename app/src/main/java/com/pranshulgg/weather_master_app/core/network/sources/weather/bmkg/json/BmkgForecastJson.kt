package com.pranshulgg.weather_master_app.core.network.sources.weather.bmkg.json

import com.google.gson.annotations.SerializedName


data class BmkgCurrentForecastJson(
    val data: BmkgCurrentForecastDataJson
)

data class BmkgCurrentForecastDataJson(
    @SerializedName("cuaca") val weather: BmkgCurrentForecastWeatherJson
)

data class BmkgCurrentForecastWeatherJson(
    val weather: Int,
    @SerializedName("weather_desc_en") val weatherDescription: String?,
    val datetime: String,
    @SerializedName("t") val temperature: Double,
    @SerializedName("wd_deg") val windDirection: Double?,
    @SerializedName("ws") val windSpeed: Double?,
    @SerializedName("hu") val humidity: Double?,
    @SerializedName("vs") val visibility: Double?
)

// FORECAST

data class BmkgForecastJson(
    val data: List<BmkgForecastDataJson>
)

data class BmkgForecastDataJson(
    @SerializedName("cuaca")
    val weather: List<List<BmkgForecastWeatherJson>>
)

data class BmkgForecastWeatherJson(
    val datetime: String,
    val t: Double?,
    val tcc: Double,
    val tp: Double?,
    val weather: Int,

    @SerializedName("weather_desc")
    val weatherDesc: String,

    @SerializedName("weather_desc_en")
    val weatherDescEn: String,

    @SerializedName("wd_deg")
    val windDegree: Double?,

    val wd: String?,

    @SerializedName("wd_to")
    val windTo: String,

    val ws: Double?,
    val hu: Double?,
    val vs: Double?,

    @SerializedName("local_datetime")
    val localDatetime: String
)