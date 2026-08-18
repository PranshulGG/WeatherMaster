package com.pranshulgg.weather_master_app.core.network.sources.weather.pirateweather.json

import com.google.gson.annotations.SerializedName

data class PirateWeatherDailyJson(
    val summary: String?,
    val icon: String?,
    val data: List<PirateWeatherDailyDataJson>
)

data class PirateWeatherDailyDataJson(
    val time: Long,
    val summary: String?,
    val icon: String?,

    @SerializedName("sunriseTime") val sunriseTime: Long?,
    @SerializedName("sunsetTime") val sunsetTime: Long?,
    @SerializedName("moonPhase") val moonPhase: Double,

    @SerializedName("temperatureMin") val temperatureMin: Double?,
    @SerializedName("temperatureMax") val temperatureMax: Double?,
    @SerializedName("temperatureHigh") val temperatureHigh: Double?,
    @SerializedName("temperatureLow") val temperatureLow: Double?,

    @SerializedName("apparentTemperatureMin") val apparentTemperatureMin: Double?,
    @SerializedName("apparentTemperatureMax") val apparentTemperatureMax: Double?,

    @SerializedName("dewPoint") val dewPoint: Double?,
    val humidity: Double?,
    val pressure: Double?,

    @SerializedName("windSpeed") val windSpeed: Double?,
    @SerializedName("windGust") val windGust: Double?,
    @SerializedName("windBearing") val windBearing: Int?,

    @SerializedName("cloudCover") val cloudCover: Double?,
    @SerializedName("uvIndex") val uvIndex: Double?,
    val visibility: Double?,

    @SerializedName("precipIntensity") val precipIntensity: Double?,
    @SerializedName("precipIntensityMax") val precipIntensityMax: Double?,
    @SerializedName("precipProbability") val precipProbability: Double?,
    @SerializedName("precipAccumulation") val precipAccumulation: Double?,
    @SerializedName("precipType") val precipType: String?
)
