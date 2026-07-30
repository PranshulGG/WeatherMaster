package com.pranshulgg.weather_master_app.data.local.entity.alerts

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertSeverity
import com.pranshulgg.weather_master_app.data.local.entity.location.WeatherLocationEntity


@Entity(
    tableName = "alerts",
    foreignKeys = [
        ForeignKey(
            entity = WeatherLocationEntity::class,
            parentColumns = ["id"],
            childColumns = ["locationId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [Index("locationId")],
)
data class AlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val locationId: String,

    val event: String,
    val severity: AlertSeverity?,
    val effective: Long?,
    val expires: Long?,
    val description: String,
    val source: String?,
    val lastUpdatedInMilli: Long
)
