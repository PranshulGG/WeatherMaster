package com.pranshulgg.weathermaster.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pranshulgg.weathermaster.core.model.WeatherProviders
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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
    val provider: WeatherProviders
)