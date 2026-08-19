package com.pranshulgg.weather_master_app.data.local.entity.location

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.openmeteo.OpenMeteoModel
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
    val source: Source,
    val state: String? = null,
    val isFavorite: Boolean = false,
    val isPinned: Boolean = false,
    val countryCode: String? = null,
    val isDefault: Boolean = false,
    val isDeviceLocation: Boolean = false,
    val alertSource: Source,
    val airQualitySource: Source,
    val customName: String? = null,

    @ColumnInfo(name = "openMeteoModel", defaultValue = "BEST_MATCH")
    val openMeteoModel: OpenMeteoModel = OpenMeteoModel.BEST_MATCH,
    val alertsLastFetchedAt: Long? = null
)