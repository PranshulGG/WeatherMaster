package com.pranshulgg.weather_master_app.core.network.sources.weather.openweather

import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition


object OpenWeatherConditionMap {
    fun getCondition(icon: String?): WeatherCondition {
        return when (icon) {
            "01d", "01n" -> WeatherCondition.CLEAR_SKY
            "02d", "02n" -> WeatherCondition.MOSTLY_CLEAR
            "03d", "03n" -> WeatherCondition.PARTLY_CLOUDY
            "04d", "04n" -> WeatherCondition.OVERCAST
            "09d", "09n" -> WeatherCondition.RAIN
            "10d", "10n" -> WeatherCondition.RAIN
            "11d", "11n" -> WeatherCondition.THUNDERSTORM
            "13d", "13n" -> WeatherCondition.SNOW
            "50d", "50n" -> WeatherCondition.FOG_HAZE
            else -> WeatherCondition.NO_CONDITION_FOUND
        }
    }
}