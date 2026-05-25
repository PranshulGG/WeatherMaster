package com.pranshulgg.weather_master_app.core.network.sources.weather.metnorway

import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition


// FROM https://nrkno.github.io/yr-weather-symbols/
object MetNorwayWeatherConditionMap {
    fun getCondition(id: String?): WeatherCondition {
        return when (id?.substringBefore("_")) {
            "clearsky", "fair" ->
                WeatherCondition.CLEAR_SKY

            "partlycloudy" ->
                WeatherCondition.PARTLY_CLOUDY

            "cloudy" ->
                WeatherCondition.OVERCAST

            "lightrainshowers",
            "lightrainshowersandthunder",
            "lightrain" ->
                WeatherCondition.LIGHT_RAIN

            "rainshowers",
            "rainshowersandthunder",
            "rain",
            "sleet" ->
                WeatherCondition.RAIN

            "heavyrainshowers",
            "lightsleetshowers",
            "sleetshowers",
            "heavysleetshowers",
            "heavyrain",
            "heavysleet" ->
                WeatherCondition.HEAVY_RAIN

            "lightsnowshowers",
            "lightsnow",
            "lightsleet" ->
                WeatherCondition.LIGHT_SNOW

            "snowshowers",
            "snow" ->
                WeatherCondition.SNOW

            "heavysnowshowers",
            "heavysnow" ->
                WeatherCondition.HEAVY_SNOW

            "heavysnowandthunder",
            "snowandthunder",
            "lightsnowandthunder",
            "heavysleetandthunder",
            "sleetandthunder",
            "lightsleetandthunder",
            "lightrainandthunder",
            "rainandthunder",
            "heavyrainandthunder",
            "lightssnowshowersandthunder",
            "snowshowersandthunder",
            "heavysnowshowersandthunder",
            "heavyrainshowersandthunder",
            "lightssleetshowersandthunder",
            "sleetshowersandthunder",
            "heavysleetshowersandthunder" ->
                WeatherCondition.THUNDERSTORM

            "fog" ->
                WeatherCondition.FOG_HAZE

            else ->
                WeatherCondition.NO_CONDITION_FOUND
        }
    }
}