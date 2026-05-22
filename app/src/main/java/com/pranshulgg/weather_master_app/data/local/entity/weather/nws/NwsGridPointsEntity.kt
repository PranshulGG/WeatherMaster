package com.pranshulgg.weather_master_app.data.local.entity.weather.nws

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.pranshulgg.weather_master_app.data.local.entity.location.WeatherLocationEntity

// NWS WANTS TO BE DIFFERENT :/
@Entity(
    tableName = "nws_location_gridpoints",
    foreignKeys = [
        ForeignKey(
            entity = WeatherLocationEntity::class,
            parentColumns = ["id"],
            childColumns = ["locationId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [Index("locationId")],
    primaryKeys = ["locationId"]
)
data class NwsGridPointsEntity(
    val locationId: String,
    val officeId: String,
    val gridX: Long,
    val gridY: Long,
    val stationIdentifier: String,
    val lastUpdatedMilli: Long

)