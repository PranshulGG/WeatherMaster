package com.pranshulgg.weather_master_app.core.model.domain.location

import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.openmeteo.OpenMeteoModel

data class Location(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val timezone: String,
    val countryCode: String?,
    val state: String,
    val source: Source = Source.OPEN_METEO,
    val isFavorite: Boolean = false,
    val isPinned: Boolean = false,
    val isDefault: Boolean,
    val isDeviceLocation: Boolean = false,
    val alertSource: Source = Source.NONE,
    val airQualitySource: Source = Source.OPEN_METEO,
    val customName: String? = null,
    val openMeteoModel: OpenMeteoModel = OpenMeteoModel.BEST_MATCH,
    val alertsLastFetchedAt: Long? = null
)