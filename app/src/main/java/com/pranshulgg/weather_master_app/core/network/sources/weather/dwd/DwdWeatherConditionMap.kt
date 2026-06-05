package com.pranshulgg.weather_master_app.core.network.sources.weather.dwd

import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition


object DwdWeatherConditionMap {
    fun getCondition(id: String?): WeatherCondition {
        return when (id) {
            "clear-day", "clear-night", "wind" -> WeatherCondition.CLEAR_SKY
            "partly-cloudy-day", "partly-cloudy-night" -> WeatherCondition.PARTLY_CLOUDY
            "cloudy" -> WeatherCondition.OVERCAST
            "fog" -> WeatherCondition.FOG_HAZE
            "rain" -> WeatherCondition.RAIN
            "sleet", "snow", "hail" -> WeatherCondition.SNOW
            "thunderstorm" -> WeatherCondition.THUNDERSTORM
            else -> WeatherCondition.NO_CONDITION_FOUND
        }
    }
}