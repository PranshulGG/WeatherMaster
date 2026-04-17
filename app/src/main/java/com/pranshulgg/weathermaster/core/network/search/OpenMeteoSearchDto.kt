package com.pranshulgg.weathermaster.core.network.search

import com.google.gson.annotations.SerializedName

data class OpenMeteoSearchDto(
    val results: List<OpenMeteoSearchItemDto>
)

data class OpenMeteoSearchItemDto(
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
