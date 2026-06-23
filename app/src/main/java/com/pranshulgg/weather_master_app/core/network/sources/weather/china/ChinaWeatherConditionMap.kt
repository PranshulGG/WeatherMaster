package com.pranshulgg.weather_master_app.core.network.sources.weather.china

import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition

object ChinaWeatherConditionMap {
    fun getCondition(id: Int?): WeatherCondition {
        return when (id) {

            0 -> WeatherCondition.CLEAR_SKY
            1 -> WeatherCondition.PARTLY_CLOUDY
            2 -> WeatherCondition.OVERCAST

            4, 32 -> WeatherCondition.THUNDERSTORM
            5 -> WeatherCondition.THUNDERSTORM

            6, 19, 3 -> WeatherCondition.RAIN

            7, 21 -> WeatherCondition.LIGHT_RAIN
            8 -> WeatherCondition.RAIN
            9, 10, 11, 12, 23, 24, 25 -> WeatherCondition.HEAVY_RAIN

            13 -> WeatherCondition.SNOW
            14, 26 -> WeatherCondition.LIGHT_SNOW
            15, 27 -> WeatherCondition.SNOW
            16, 28 -> WeatherCondition.HEAVY_SNOW
            17 -> WeatherCondition.HEAVY_SNOW

            18, 35, 53 -> WeatherCondition.FOG_HAZE

            20, 29, 30, 31 -> WeatherCondition.VERY_HOT

            33 -> WeatherCondition.THUNDERSTORM

            34 -> WeatherCondition.HEAVY_SNOW

            99 -> WeatherCondition.NO_CONDITION_FOUND

            else -> WeatherCondition.NO_CONDITION_FOUND
        }
    }
}