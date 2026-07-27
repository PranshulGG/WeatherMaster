package com.pranshulgg.weather_master_app.core.network.sources.airquality.accu.json

import com.google.gson.annotations.SerializedName

data class AccuAqiJson(
    val data: AccuAqiDataJson,
    @SerializedName("epochDate") val timeSeconds: Long
)

data class AccuAqiDataJson(
    val pollutants: List<AccuAqiPollutantJson>
)

data class AccuAqiPollutantJson(
    val type: String,
    val concentration: AccuAqiConcentrationJson
)

data class AccuAqiConcentrationJson(
    val value: Double?
)

data class AccuAqiForecastJson(
    val data: List<AccuAqiForecastDataJson>
)

data class AccuAqiForecastDataJson(
    @SerializedName("epochDate") val timeSeconds: Long,
    val pollutants: List<AccuAqiForecastPollutantJson>
)

data class AccuAqiForecastPollutantJson(
    val type: String,
    val concentration: AccuAqiConcentrationJson
)