package com.pranshulgg.weather_master_app.data.local.entity.weather.blocks

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlockType

@Entity(tableName = "weather_blocks")
data class WeatherBlockEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val isDaily: Boolean = false,

    val type: WeatherBlockType,

    val isHidden: Boolean = false,

    val position: Int
)