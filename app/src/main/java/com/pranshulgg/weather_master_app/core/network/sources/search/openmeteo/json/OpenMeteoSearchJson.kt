package com.pranshulgg.weather_master_app.core.network.sources.search.openmeteo.json

import com.google.gson.annotations.SerializedName

data class OpenMeteoSearchJson(
    val results: List<OpenMeteoSearchItemJson>?
)

data class OpenMeteoSearchItemJson(
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val country: String,

    @SerializedName("country_code")
    val countryCode: String,

    @SerializedName("admin1")
    val state: String?,
    @SerializedName("admin2")
    val state2: String?,

    val timezone: String
)
