package com.pranshulgg.weather_master_app.core.utils.weather.computing.summary

import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition


data class SummaryPeakRain(
    val at: Long,
    val amount: Double,
    val probability: Int
)

data class SummaryPeakUv(
    val uv: Double,
    val at: Long,
)

data class SummaryTemps(
    val max: Double,
    val min: Double,
    val avg: Double,
)

data class SummaryPeakSnow(
    val at: Long,
    val amount: Double,
    val probability: Int
)

data class SummaryData(
    val rainDay: SummaryPeakRain,
    val rainNight: SummaryPeakRain,
    val uv: SummaryPeakUv,
    val snowDay: SummaryPeakSnow,
    val snowNight: SummaryPeakSnow,
    val conditionDay: String,
    val conditionNight: String,
    val temps: SummaryTemps

)
