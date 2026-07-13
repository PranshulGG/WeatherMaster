package com.pranshulgg.weather_master_app.core.network.sources.weather.meteoam.json

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.json.JsonElement

data class MeteoamCurrentWeatherJson(
    val datasets: MeteoamCurrentWeatherDatasetsJson,
    @SerializedName("timeseries") val timeSeries: List<List<String>>
)

data class MeteoamCurrentWeatherDatasetsJson(
    @SerializedName("0") val current: MeteoamCurrentWeatherDatasetValueJson
)

data class MeteoamCurrentWeatherDatasetValueJson(
    @SerializedName("0") val temperature: MeteoamCurrentWeatherDatasetValueTemperatureJson,
    @SerializedName("1") val humidity: MeteoamCurrentWeatherDatasetValueHumidityJson,
    @SerializedName("2") val pressure: MeteoamCurrentWeatherDatasetValuePressureJson,
    @SerializedName("3") val windDirection: MeteoamCurrentWeatherDatasetValueWindDirectionJson,
    @SerializedName("6") val windSpeedKmh: MeteoamCurrentWeatherDatasetValueWindSpeedJson,
    @SerializedName("8") val icon: MeteoamCurrentWeatherDatasetValueIconJson
)


data class MeteoamCurrentWeatherDatasetValueTemperatureJson(
    @SerializedName("0") val value: Double?
)


data class MeteoamCurrentWeatherDatasetValueHumidityJson(
    @SerializedName("0") val value: Double?
)

data class MeteoamCurrentWeatherDatasetValuePressureJson(
    @SerializedName("0") val value: Double?
)

data class MeteoamCurrentWeatherDatasetValueWindDirectionJson(
    @SerializedName("0") val value: String? // Could be either "VRB" or a number
)

data class MeteoamCurrentWeatherDatasetValueWindSpeedJson(
    @SerializedName("0") val value: Double?
)

data class MeteoamCurrentWeatherDatasetValueIconJson(
    @SerializedName("0") val value: String?
)


// FORECAST

data class MeteoamForecastWeatherJson(
    val datasets: MeteoamForecastWeatherDatasetsJson,
    @SerializedName("timeseries") val timeSeries: List<String>
)

data class MeteoamForecastWeatherDatasetsJson(
    @SerializedName("0") val forecast: MeteoamForecastWeatherDataJson
)

data class MeteoamForecastWeatherDataJson(
    @SerializedName("0") val temperature: Map<String, Double?>?,
    @SerializedName("1") val humidity: Map<String, Double?>?,
    @SerializedName("2") val pressure: Map<String, Double?>?,
    @SerializedName("3") val precipitationProbability: Map<String, Double?>?, // why is it ttp?
    @SerializedName("4") val windDirection: Map<String, String?>?,
    @SerializedName("7") val windSpeedKmh: Map<String, Double?>?,
    @SerializedName("9") val icon: Map<String, String>
)
