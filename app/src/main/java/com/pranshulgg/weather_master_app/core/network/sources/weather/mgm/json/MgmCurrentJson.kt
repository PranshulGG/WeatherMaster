package com.pranshulgg.weather_master_app.core.network.sources.weather.mgm.json

import com.google.gson.annotations.SerializedName

data class MgmCurrentJson(
    @SerializedName("hadiseKodu") val condition: String?,
    @SerializedName("sicaklik") val temperature: Double?,
    @SerializedName("hissedilenSicaklik") val feelsLike: Double?,
    @SerializedName("nem") val humidity: Double?,
    @SerializedName("ruzgarHiz") val windSpeed: Double?,
    @SerializedName("ruzgarYon") val windDirection: Double?,
    @SerializedName("denizeIndirgenmisBasinc") val pressureMsl: Double?,
    @SerializedName("gorus") val visibility: Double?,
    @SerializedName("veriZamani") val time: String?,
)
