package com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.json

import com.google.gson.annotations.SerializedName

data class MgmHourlyResultJson(
    @SerializedName("tahmin") val forecast: List<MgmHourlyForecastJson>?,
)

data class MgmHourlyForecastJson(
    // Carries a 'Z' suffix but is actually Europe/Istanbul local time, not UTC - see MgmDailyJson.
    @SerializedName("tarih") val time: String?,
    @SerializedName("hadise") val condition: String?,
    @SerializedName("sicaklik") val temperature: Double?,
    @SerializedName("hissedilenSicaklik") val feelsLike: Double?,
    @SerializedName("nem") val humidity: Double?,
    @SerializedName("ruzgarYonu") val windDirection: Double?,
    @SerializedName("ruzgarHizi") val windSpeed: Double?,
)
