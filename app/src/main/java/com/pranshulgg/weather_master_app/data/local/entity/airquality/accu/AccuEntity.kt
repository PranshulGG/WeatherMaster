package com.pranshulgg.weather_master_app.data.local.entity.airquality.accu

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.pranshulgg.weather_master_app.data.local.entity.location.WeatherLocationEntity

@Entity(
    tableName = "accu_locations",
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
data class AccuEntity(
    val locationId: String,
    val cityKey: String
)