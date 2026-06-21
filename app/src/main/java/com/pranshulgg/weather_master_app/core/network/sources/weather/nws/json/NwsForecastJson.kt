package com.pranshulgg.weather_master_app.core.network.sources.weather.nws.json

import com.google.gson.annotations.SerializedName

// ---------------------------- DAILY ----------------------------

data class NwsForecastJson(
    val properties: NwsForecastPeriodsJson
)

data class NwsForecastPeriodsJson(
    val periods: List<NwsForecastPeriodsItemJson>
)

data class NwsForecastPeriodsItemJson(
    val number: Long,
    val startTime: String,
    val endTime: String,

    @SerializedName("isDaytime")
    val isDayTime: Boolean,
    val temperature: Double,
    val windSpeed: String,
    val windDirection: String,
    val icon: String?,
    val probabilityOfPrecipitation: NwsForecastProbabilityOfPrecipitationJson
)

data class NwsForecastProbabilityOfPrecipitationJson(
    val value: Long
)

// ---------------------------- CURRENT ----------------------------

data class NwsCurrentForecastJson(
    val properties: NwsCurrentForecastPropertiesJson,
)

data class NwsCurrentForecastPropertiesJson(
    val temperature: NwsCurrentForecastTemperatureJson,
    val windSpeed: NwsCurrentForecastWindSpeedJson,
    val seaLevelPressure: NwsCurrentForecastSeaLevelPressureJson,
    val visibility: NwsCurrentForecastVisibilityJson,
    val relativeHumidity: NwsCurrentForecastRelativeHumidityJson,
    val timestamp: String,
    val icon: String?,
)

data class NwsCurrentForecastTemperatureJson(
    val value: Double?
)

data class NwsCurrentForecastWindSpeedJson(
    val value: Double?
)

data class NwsCurrentForecastSeaLevelPressureJson(
    val value: Double?
)

data class NwsCurrentForecastVisibilityJson(
    val value: Double?
)

data class NwsCurrentForecastRelativeHumidityJson(
    val value: Double
)

// ---------------------------- HOURLY ----------------------------

data class NwsHourlyForecastJson(
    val properties: NwsHourlyForecastPeriodsJson,
)

data class NwsHourlyForecastPeriodsJson(
    val periods: List<NwsHourlyForecastPeriodsItemJson>,
)


data class NwsHourlyForecastPeriodsItemJson(
    val number: Long,
    val startTime: String,
    val endTime: String,
    val temperature: Double,
    val probabilityOfPrecipitation: NwsHourlyForecastProbabilityOfPrecipitationJson,

    @SerializedName("dewpoint")
    val dewPoint: NwsHourlyForecastDewPointJson,

    val relativeHumidity: NwsHourlyForecastRelativeHumidityJson,

    val windSpeed: String,
    val windDirection: String,
    val icon: String?

)

data class NwsHourlyForecastProbabilityOfPrecipitationJson(
    val value: Long?
)

data class NwsHourlyForecastDewPointJson(
    val value: Double?
)

data class NwsHourlyForecastRelativeHumidityJson(
    val value: Double?
)