package com.pranshulgg.weather_master_app.core.network.sources.weather.buienradar.json

import com.google.gson.annotations.SerializedName

data class BuienradarGraphJson(
    val unit: String?,
    val fillColor: String?,
    val forecasts: List<BuienradarGraphForecastEntry>?
)

data class BuienradarGraphForecastEntry(
    /** Local time of the location, e.g. "2026-08-28T17:40:00" */
    val dateTime: String?,
    /** UTC time, e.g. "2026-08-28T15:40:00" */
    val utcDateTime: String?,
    /** Rain rate in the graph unit (mm/h) */
    val dataValue: Double?,
    /** Buienradar's 0-100 intensity percentage */
    @SerializedName("percentageValue") val percentageValue: Double?
)