package com.pranshulgg.weather_master_app.core.network.sources.weather.accu.json.bundle

import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.json.AccuCurrentWeatherJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.json.AccuDailyWeatherJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.json.AccuHourlyWeatherJson

data class AccuWeatherBundle(
    val current: AccuCurrentWeatherJson,
    val hourly: List<AccuHourlyWeatherJson>,
    val daily: AccuDailyWeatherJson
)