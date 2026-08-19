package com.pranshulgg.weather_master_app.data.local.entity.weather

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pranshulgg.weather_master_app.core.model.sources.Source


@Entity(tableName = "api_keys")
data class ApiKeyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val source: Source,
    val apiKey: String? = null
)