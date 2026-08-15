package com.pranshulgg.weather_master_app.core.network.sources.weather.metoffice.json

import com.google.gson.annotations.SerializedName

data class MetOfficeHourlyForecastJson(
    val features: List<MetOfficeHourlyForecastFeatureJson>
)

data class MetOfficeHourlyForecastFeatureJson(
    val properties: MetOfficeHourlyForecastPropertiesJson
)

data class MetOfficeHourlyForecastPropertiesJson(
    val timeSeries: List<MetOfficeHourlyForecastTimeSeriesJson>
)

data class MetOfficeHourlyForecastTimeSeriesJson(
    val time: String,
    val screenTemperature: Double?,
    val screenDewPointTemperature: Double?,
    val feelsLikeTemperature: Double?,
    @SerializedName("windSpeed10m") val windSpeed10mMs: Double?,
    val windDirectionFrom10m: Int?,
    @SerializedName("visibility") val visibilityM: Long?,
    val screenRelativeHumidity: Double?,
    @SerializedName("mslp") val pressurePa: Long?,
    val uvIndex: Double?,
    val significantWeatherCode: Int?,
    val totalPrecipAmount: Double?,
    val totalSnowAmount: Double?,
    val probOfPrecipitation: Double

)