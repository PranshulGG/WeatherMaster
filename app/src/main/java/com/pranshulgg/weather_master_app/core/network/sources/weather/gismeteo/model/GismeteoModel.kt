package com.pranshulgg.weather_master_app.core.network.sources.weather.gismeteo.model

data class GismeteoModel(
    val current: GismeteoModelCurrent,
    val hourly: List<GismeteoModelHourly>,
    val daily: List<GismeteoModelDaily>
)

data class GismeteoModelCurrent(
    val temperature: Double?,
    val feelsLike: Double?,
    val pressureMmHg: Double?,
    val windSpeedMs: Double?,
    val windDirection: Int?,
    val humidity: Double?,
    val icon: String?,
    val time: String
)

data class GismeteoModelHourly(
    val temperature: Double?,
    val pressureMmHg: Double?,
    val windSpeedMs: Double?,
    val humidity: Double?,
    val windDirection: Int?,
    val icon: String?,
    val precipitationType: Int?,
    val precipitation: Double?,
    val time: String
)

data class GismeteoModelDaily(
    val temperatureMin: Double?,
    val temperatureMax: Double?,
    val icon: String?,
    val precipitationType: Int?,
    val windDirection: Int?,
    val precipitation: Double?,
    val time: String
)
