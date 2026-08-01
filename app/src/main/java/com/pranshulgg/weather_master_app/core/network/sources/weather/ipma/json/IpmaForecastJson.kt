package com.pranshulgg.weather_master_app.core.network.sources.weather.ipma.json

import com.google.gson.annotations.SerializedName

data class IpmaForecastJson(
    @SerializedName("tMed") val temperature: String?,
    @SerializedName("tMin") val minTemperature: String?,
    @SerializedName("tMax") val maxTemperature: String?,
    @SerializedName("dataPrev") val date: String,
    @SerializedName("ddVento") val windDirection: String?,
    @SerializedName("ffVento") val windSpeed: String?,
    @SerializedName("probabilidadePrecipita") val precipitationProbability: String?,
    @SerializedName("iUv") val uvIndex: String?,
    @SerializedName("hR") val humidity: String?,
    @SerializedName("idTipoTempo") val weatherCode: Int?,

    )