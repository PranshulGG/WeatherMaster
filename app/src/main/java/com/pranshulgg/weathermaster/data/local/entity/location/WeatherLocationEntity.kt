package com.pranshulgg.weathermaster.data.local.entity.location

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pranshulgg.weathermaster.core.model.sources.WeatherSource
import kotlin.uuid.ExperimentalUuidApi

@Entity(tableName = "weather_locations")
@OptIn(ExperimentalUuidApi::class)
data class WeatherLocationEntity(
    @PrimaryKey
    val id: String,

    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val source: WeatherSource,
    val state: String? = null,
    val isFavorite: Boolean = false,
    val isPinned: Boolean = false,
    val countryCode: String? = null,
    val isDefault: Boolean = false,
    val isDeviceLocation: Boolean = false

)