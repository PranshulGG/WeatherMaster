package com.pranshulgg.weather_master_app.data.local.converters

import androidx.room.TypeConverter
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlockType

class WeatherBlockTypeConverter {
    @TypeConverter
    fun fromString(value: String): WeatherBlockType? {
        return WeatherBlockType.fromString(value)
    }

    @TypeConverter
    fun toString(type: WeatherBlockType?): String {
        return type?.name ?: ""
    }
}