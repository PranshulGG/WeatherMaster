package com.pranshulgg.weather_master_app.core.network.sources.weather.pirateweather.json

data class PirateWeatherHourlyJson(
    val summary: String?,
    val icon: String?,
    val data: List<PirateWeatherDataPointJson>
)
