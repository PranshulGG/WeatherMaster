package com.pranshulgg.weather_master_app.core.network.sources.weather.aemet.model

import com.pranshulgg.weather_master_app.core.network.sources.weather.aemet.json.AemetDailyForecastRootJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.aemet.json.AemetHourlyForecastRootJson

data class AemetForecastJson(
    val daily: AemetDailyForecastRootJson,
    val hourly: AemetHourlyForecastRootJson
)
