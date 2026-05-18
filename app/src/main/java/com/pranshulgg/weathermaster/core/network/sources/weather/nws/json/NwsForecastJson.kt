package com.pranshulgg.weathermaster.core.network.sources.weather.nws.json


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
    val isDayTime: Boolean,
    val temperature: Long,
    val windSpeed: String,
    val windDirection: String,
    val shortForecast: String,
    val probabilityOfPrecipitation: NwsForecastProbabilityOfPrecipitationJson
)

data class NwsForecastProbabilityOfPrecipitationJson(
    val value: Long
)

