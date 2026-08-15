package com.pranshulgg.weather_master_app.core.network.sources.weather.metoffice

import androidx.compose.foundation.layout.Box
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition


object MetOfficeWeatherConditionMap {
    fun getCondition(id: Int?): WeatherCondition {
        return when (id) {
            -1 -> WeatherCondition.LIGHT_RAIN
            0, 1 -> WeatherCondition.CLEAR_SKY
            2, 3 -> WeatherCondition.PARTLY_CLOUDY
            5, 6 -> WeatherCondition.FOG_HAZE
            7, 8 -> WeatherCondition.OVERCAST
            9, 10, 11, 12 -> WeatherCondition.RAIN
            13, 14, 15 -> WeatherCondition.HEAVY_RAIN
            16, 17, 18 -> WeatherCondition.SLEET
            19, 20, 21 -> WeatherCondition.HAIL
            22, 23, 24 -> WeatherCondition.SNOW
            25, 26, 27 -> WeatherCondition.HEAVY_SNOW
            28, 29, 30 -> WeatherCondition.THUNDERSTORM
            else -> WeatherCondition.NO_CONDITION_FOUND
        }
    }
}