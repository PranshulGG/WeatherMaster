package com.pranshulgg.weather_master_app.core.model.domain.weather

import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.data.local.entity.weather.ApiKeyEntity

data class ApiKey(
    val id: Long,
    val source: Source,
    val apiKey: String? = null
)