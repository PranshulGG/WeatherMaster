package com.pranshulgg.weather_master_app.core.network.sources.weather.smhi

import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition

object SmhiWeatherConditionMap {

    fun getCondition(code: Int?): WeatherCondition {
        return when (code) {
            1, 2 -> WeatherCondition.CLEAR_SKY
            3, 5 -> WeatherCondition.PARTLY_CLOUDY
            4 -> WeatherCondition.MOSTLY_CLEAR
            6 -> WeatherCondition.OVERCAST
            7 -> WeatherCondition.FOG_HAZE
            8, 18 -> WeatherCondition.LIGHT_RAIN
            9, 19 -> WeatherCondition.RAIN
            10, 20 -> WeatherCondition.HEAVY_RAIN
            11, 21 -> WeatherCondition.THUNDERSTORM
            12, 15, 22, 25 -> WeatherCondition.LIGHT_SNOW
            13, 16, 23, 26 -> WeatherCondition.SNOW
            14, 17, 24, 27 -> WeatherCondition.HEAVY_SNOW
            else -> WeatherCondition.NO_CONDITION_FOUND
        }
    }

}