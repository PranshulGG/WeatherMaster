package com.pranshulgg.weather_master_app.core.network.sources.weather.pirateweather.json

import com.google.gson.annotations.SerializedName

data class PirateWeatherJson(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val offset: Double,
    val elevation: Double?,

    val currently: PirateWeatherCurrentJson,
    val hourly: PirateWeatherHourlyJson,
    val daily: PirateWeatherDailyJson,
    val alerts: List<PirateWeatherAlertJson>?
)
