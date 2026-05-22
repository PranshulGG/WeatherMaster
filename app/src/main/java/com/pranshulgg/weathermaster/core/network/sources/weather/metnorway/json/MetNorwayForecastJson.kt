package com.pranshulgg.weathermaster.core.network.sources.weather.metnorway.json

import com.google.gson.annotations.SerializedName

data class MetNorwayForecastJson(
    val properties: MetNorwayForecastPropertiesJson
)

data class MetNorwayForecastPropertiesJson(
    @SerializedName("timeseries")
    val data: List<MetNorwayForecastTimeSeriesJson>
)

data class MetNorwayForecastTimeSeriesJson(
    val time: String,
    val data: MetNorwayForecastDataJson
)

data class MetNorwayForecastDataJson(
    val instant: MetNorwayForecastInstantJson,
    @SerializedName("next_1_hours")
    val nextHour: MetNorwayForecastNextHourJson?,
    @SerializedName("next_6_hours")
    val next6Hours: MetNorwayForecastNextHourJson?,
    @SerializedName("next_12_hours")
    val next12Hours: MetNorwayForecastNextHourJson?
)

data class MetNorwayForecastInstantJson(
    val details: MetNorwayForecastDetailsJson
)

data class MetNorwayForecastDetailsJson(
    @SerializedName("air_pressure_at_sea_level")
    val pressureMsl: Double,
    @SerializedName("air_temperature")
    val temperature: Double,
    @SerializedName("dew_point_temperature")
    val dewPoint: Double,
    @SerializedName("relative_humidity")
    val relativeHumidity: Double,
    @SerializedName("ultraviolet_index_clear_sky")
    val uvIndex: Double,
    @SerializedName("wind_speed")
    val windSpeed: Double,
    @SerializedName("wind_from_direction")
    val windDirection: Double
)

data class MetNorwayForecastNextHourJson(
    val summary: MetNorwayForecastNextHourSummaryJson,
    val details: MetNorwayForecastNextHourDetailsJson
)

data class MetNorwayForecastNextHourSummaryJson(
    @SerializedName("symbol_code")
    val symbolCode: String
)

data class MetNorwayForecastNextHourDetailsJson(
    @SerializedName("precipitation_amount")
    val precipitationAmount: Double?
)