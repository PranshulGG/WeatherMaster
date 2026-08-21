package com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json.bundle

import com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json.OpenWeatherCurrentJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json.OpenWeatherForecastJson

data class OpenWeatherJsonBundle(
    val current: OpenWeatherCurrentJson,
    val forecast: OpenWeatherForecastJson
)