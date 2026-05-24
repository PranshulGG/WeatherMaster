package com.pranshulgg.weather_master_app.core.network.sources.weather.smhi.json

import com.google.gson.annotations.SerializedName

data class SmhiForecastJson(
    val timeSeries: List<SmhiForecastTimeSeriesJson>
)

data class SmhiForecastTimeSeriesJson(
    val time: String,
    val data: SmhiForecastTimeSeriesDataJson
)

data class SmhiForecastTimeSeriesDataJson(
    @SerializedName("air_temperature")
    val temperature: Double,
    @SerializedName("wind_from_direction")
    val windDirection: Int,
    @SerializedName("wind_speed")
    val windSpeed: Double,
    @SerializedName("relative_humidity")
    val humidity: Int,
    @SerializedName("air_pressure_at_mean_sea_level")
    val pressureMsl: Double,
    @SerializedName("visibility_in_air")
    val visibility: Double,
    @SerializedName("probability_of_precipitation")
    val precipitationProbability: Int,
    @SerializedName("precipitation_amount_max")
    val precipitationAmountMax: Double,
    @SerializedName("symbol_code")
    val symbolCode: Int,
    @SerializedName("predominant_precipitation_type_at_surface")
    val precipitationType: Int
)

