package com.pranshulgg.weather_master_app.core.network.sources.weather.pirateweather

import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition

object PirateWeatherConditionMap {

    fun getCondition(icon: String?): WeatherCondition {
        return when (icon) {
            "clear-day", "clear-night" -> WeatherCondition.CLEAR_SKY

            "partly-cloudy-day", "partly-cloudy-night" -> WeatherCondition.PARTLY_CLOUDY

            "cloudy" -> WeatherCondition.OVERCAST

            "fog" -> WeatherCondition.FOG_HAZE

            "wind" -> WeatherCondition.MOSTLY_CLEAR

            "rain" -> WeatherCondition.RAIN

            "snow" -> WeatherCondition.SNOW

            "sleet" -> WeatherCondition.SLEET

            "thunderstorm" -> WeatherCondition.THUNDERSTORM

            else -> WeatherCondition.NO_CONDITION_FOUND
        }
    }
}
