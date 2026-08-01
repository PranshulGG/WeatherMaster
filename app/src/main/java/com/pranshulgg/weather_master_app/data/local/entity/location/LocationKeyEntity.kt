package com.pranshulgg.weather_master_app.data.local.entity.location

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

// Sources that use keys to get the data. We will save them to avoid making multiple requests
@Entity(
    tableName = "location_keys",
    foreignKeys = [
        ForeignKey(
            entity = WeatherLocationEntity::class,
            parentColumns = ["id"],
            childColumns = ["locationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("locationId")],
    primaryKeys = ["locationId"]
)
data class LocationKeyEntity(
    val locationId: String,
    val cityKey: String
)