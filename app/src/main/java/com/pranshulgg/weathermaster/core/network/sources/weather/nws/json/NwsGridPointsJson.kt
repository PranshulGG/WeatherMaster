package com.pranshulgg.weathermaster.core.network.sources.weather.nws.json

import com.google.gson.annotations.SerializedName


data class NwsGridPointsJson(
    @SerializedName("properties")
    val points: NwsGridPointsValuesJson
)

data class NwsGridPointsValuesJson(
    @SerializedName("gridId")
    val officeId: String,
    val gridX: Long,
    val gridY: Long
)