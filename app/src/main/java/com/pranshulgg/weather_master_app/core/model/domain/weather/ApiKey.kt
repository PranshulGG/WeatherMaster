package com.pranshulgg.weather_master_app.core.model.domain.weather

import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.data.local.entity.weather.ApiKeyEntity

data class ApiKey(
    val id: Long,
    val source: WeatherSource,
    val apiKey: String? = null
)