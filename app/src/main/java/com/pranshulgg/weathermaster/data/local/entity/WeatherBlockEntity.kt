package com.pranshulgg.weathermaster.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pranshulgg.weathermaster.core.model.domain.WeatherBlockType

@Entity(tableName = "weather_blocks")
data class WeatherBlockEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val isDaily: Boolean = false,

    val type: WeatherBlockType,

    val isHidden: Boolean = false,

    val position: Int
)