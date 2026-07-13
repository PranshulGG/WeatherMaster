package com.pranshulgg.weather_master_app.core.network.sources.weather.meteoam

import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition


object MeteoamWeatherConditionMap {
    fun getCondition(id: String?): WeatherCondition {
        return when (id) {
            "01", "31" -> WeatherCondition.CLEAR_SKY
            "02", "03", "04", "32", "33", "34" -> WeatherCondition.PARTLY_CLOUDY
            "05", "06", "07", "35" -> WeatherCondition.OVERCAST
            "08" -> WeatherCondition.LIGHT_RAIN
            "09" -> WeatherCondition.RAIN
            "10" -> WeatherCondition.THUNDERSTORM
            "11", "12" -> WeatherCondition.LIGHT_SNOW // SLEET
            "13", "18", "36" -> WeatherCondition.FOG_HAZE
            "14" -> WeatherCondition.FOG_HAZE
            "15" -> WeatherCondition.LIGHT_SNOW // HAIL
            "16" -> WeatherCondition.SNOW
            "17", "19" -> WeatherCondition.CLEAR_SKY // WIND
            else -> WeatherCondition.NO_CONDITION_FOUND
        }
    }
}