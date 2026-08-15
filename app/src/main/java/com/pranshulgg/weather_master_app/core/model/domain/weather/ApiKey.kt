package com.pranshulgg.weather_master_app.core.model.domain.weather

import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.data.local.entity.weather.ApiKeyEntity
import java.util.concurrent.TimeUnit

data class ApiKey(
    val id: Long,
    val source: WeatherSource,
    val apiKey: String? = null,
    val savedAt: Long = System.currentTimeMillis()
)

// Currently only meaningful for sources whose keys expire (AEMET); harmless no-op elsewhere.
fun ApiKey.isNearExpiry(afterDays: Long = 75): Boolean {
    if (apiKey.isNullOrBlank()) return false
    return (System.currentTimeMillis() - savedAt) > TimeUnit.DAYS.toMillis(afterDays)
}