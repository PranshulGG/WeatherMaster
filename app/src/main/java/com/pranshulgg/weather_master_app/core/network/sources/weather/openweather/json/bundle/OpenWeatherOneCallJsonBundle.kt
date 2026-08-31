package com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json.bundle

import com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json.onecall.OpenWeatherOneCallCurrentJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json.onecall.OpenWeatherOneCallDailyJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json.onecall.OpenWeatherOneCallHourlyJson

data class OpenWeatherOneCallJsonBundle(
    val current: OpenWeatherOneCallCurrentJson,
    val hourly: OpenWeatherOneCallHourlyJson,
    val daily: OpenWeatherOneCallDailyJson
)
