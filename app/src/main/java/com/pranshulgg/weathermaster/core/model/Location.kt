package com.pranshulgg.weathermaster.core.model

data class Location(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val timezone: String,
    val countryCode: String?,
    val state: String,
    val provider: WeatherProviders = WeatherProviders.OPEN_METEO,
    val isFavorite: Boolean = false,
    val isPinned: Boolean = false,
    val isDefault: Boolean
)
