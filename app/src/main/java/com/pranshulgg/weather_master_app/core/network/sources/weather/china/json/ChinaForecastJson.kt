package com.pranshulgg.weather_master_app.core.network.sources.weather.china.json

import com.google.gson.annotations.SerializedName

data class ChinaForecastJson(
    val current: ChinaCurrentForecastJson,
    val forecastHourly: ChinaHourlyForecastJson,
    val forecastDaily: ChinaDailyForecastJson
)

data class ChinaCurrentForecastJson(
    val feelsLike: ChinaCurrentForecastFeelsLikeJson,
    val humidity: ChinaCurrentForecastHumidityJson,
    val pressure: ChinaCurrentForecastPressureJson,
    val pubTime: String,
    val temperature: ChinaCurrentForecastTemperatureJson,
    val uvIndex: String?,
    val visibility: ChinaCurrentForecastVisibilityJson,
    val weather: String?,
    val wind: ChinaCurrentForecastWindJson
)

data class ChinaCurrentForecastFeelsLikeJson(
    val value: String?
)

data class ChinaCurrentForecastHumidityJson(
    val value: String?
)

data class ChinaCurrentForecastPressureJson(
    val value: String?
)

data class ChinaCurrentForecastTemperatureJson(
    val value: String?
)

data class ChinaCurrentForecastVisibilityJson(
    val value: String?
)

data class ChinaCurrentForecastWindJson(
    val speed: ChinaCurrentForecastWindSpeedJson,
    val direction: ChinaCurrentForecastWindDirectionJson
)

data class ChinaCurrentForecastWindSpeedJson(
    val value: String?
)

data class ChinaCurrentForecastWindDirectionJson(
    val value: String?
)

// HOURLY
data class ChinaHourlyForecastJson(
    val temperature: ChinaHourlyForecastTemperatureJson,
    val weather: ChinaHourlyForecastWeatherJson,
    val wind: ChinaHourlyForecastWindJson
)

data class ChinaHourlyForecastTemperatureJson(
    val value: List<Int>
)


data class ChinaHourlyForecastWeatherJson(
    val value: List<Int>
)


data class ChinaHourlyForecastWindJson(
    val value: List<ChinaHourlyForecastWindValueJson>
)

data class ChinaHourlyForecastWindValueJson(
    val datetime: String,
    val direction: String?,
    val speed: String?
)

// DAILY
data class ChinaDailyForecastJson(
    val precipitationProbability: ChinaDailyForecastPrecipitationProbabilityJson,
    val pubTime: String,
    val temperature: ChinaDailyForecastTemperatureJson,
    val weather: ChinaDailyForecastWeatherJson,
    val wind: ChinaDailyForecastWindJson
)

data class ChinaDailyForecastPrecipitationProbabilityJson(
    val value: List<String?>
)

data class ChinaDailyForecastTemperatureJson(
    val value: List<ChinaDailyForecastTemperatureValueJson>
)

data class ChinaDailyForecastTemperatureValueJson(
    @SerializedName("from") val max: String?,
    @SerializedName("to") val min: String?,
)

data class ChinaDailyForecastWeatherJson(
    val value: List<ChinaDailyForecastWeatherValueJson>
)

data class ChinaDailyForecastWeatherValueJson(
    val from: String?,
    val to: String?,
)

data class ChinaDailyForecastWindJson(
    val direction: ChinaDailyForecastWindDirectionJson,
    val speed: ChinaDailyForecastWindSpeedJson
)

data class ChinaDailyForecastWindDirectionJson(
    val value: List<ChinaDailyForecastWindDirectionValueJson>
)

data class ChinaDailyForecastWindDirectionValueJson(
    val from: String?,
    val to: String?,
)

data class ChinaDailyForecastWindSpeedJson(
    val value: List<ChinaDailyForecastWindSpeedValueJson>
)

data class ChinaDailyForecastWindSpeedValueJson(
    val from: String?,
    val to: String?,
)