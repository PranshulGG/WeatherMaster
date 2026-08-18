package com.pranshulgg.weather_master_app.core.network.sources.weather.pirateweather.json

import com.google.gson.annotations.SerializedName

data class PirateWeatherCurrentJson(
    val time: Long,
    val summary: String?,
    val icon: String?,

    val temperature: Double,
    @SerializedName("apparentTemperature") val apparentTemperature: Double,
    @SerializedName("dewPoint") val dewPoint: Double,
    val humidity: Double,
    val pressure: Double,

    @SerializedName("windSpeed") val windSpeed: Double,
    @SerializedName("windGust") val windGust: Double?,
    @SerializedName("windBearing") val windBearing: Int,

    @SerializedName("cloudCover") val cloudCover: Double,
    @SerializedName("uvIndex") val uvIndex: Double,
    val visibility: Double,

    @SerializedName("precipIntensity") val precipIntensity: Double,
    @SerializedName("precipProbability") val precipProbability: Double,
    @SerializedName("precipType") val precipType: String
)
