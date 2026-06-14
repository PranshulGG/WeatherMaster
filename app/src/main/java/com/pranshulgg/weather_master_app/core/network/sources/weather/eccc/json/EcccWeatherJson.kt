package com.pranshulgg.weather_master_app.core.network.sources.weather.eccc.json

import com.google.gson.annotations.SerializedName

data class EcccWeatherJson(
    val observation: EcccCurrentWeatherJson,
    val hourlyFcst: EcccHourlyWeatherJson,
    val dailyFcst: EcccDailyWeatherJson,
)

// CURRENT
data class EcccCurrentWeatherJson(
    val iconCode: String,
    val timeStamp: String,
    val temperature: EcccCurrentWeatherTemperatureJson,
    val dewpoint: EcccCurrentWeatherDewPointJson,
    val feelsLike: EcccCurrentWeatherFeelsLikeJson,
    val pressure: EcccCurrentWeatherPressureJson,
    val visibility: EcccCurrentWeatherVisibilityJson,
    val humidity: String?,
    val windDirection: String?,
    val windSpeed: EcccCurrentWeatherWindSpeedJson
)

data class EcccCurrentWeatherTemperatureJson(
    val metric: String?,
)

data class EcccCurrentWeatherDewPointJson(
    val metric: String?,
)

data class EcccCurrentWeatherFeelsLikeJson(
    val metric: String?,
)

data class EcccCurrentWeatherPressureJson(
    val imperial: String?,
)

data class EcccCurrentWeatherVisibilityJson(
    val metric: String?,
)

data class EcccCurrentWeatherWindSpeedJson(
    val metric: String?,
)

// HOURLY

data class EcccHourlyWeatherJson(
    val hourly: List<EcccHourlyWeatherItemJson>
)

data class EcccHourlyWeatherItemJson(
    val epochTime: Long,
    val windDir: String?,
    val temperature: EcccHourlyWeatherItemTemperatureJson,
    @SerializedName("precip") val precipProbability: String?,
    val iconCode: String,
    val windSpeed: EcccHourlyWeatherItemWindSpeedJson


)

data class EcccHourlyWeatherItemTemperatureJson(
    val metric: String?,
)

data class EcccHourlyWeatherItemWindSpeedJson(
    val metric: String?,
)

// DAILY

data class EcccDailyWeatherJson(
    val daily: List<EcccDailyWeatherItemJson>
)

data class EcccDailyWeatherItemJson(
    val date: String,
    val temperature: EcccDailyWeatherItemTemperatureJson,
    @SerializedName("precip") val precipProbability: String?,
    val iconCode: String,
    val periodLabel: String,

    )

data class EcccDailyWeatherItemTemperatureJson(
    val metric: String?,
)

