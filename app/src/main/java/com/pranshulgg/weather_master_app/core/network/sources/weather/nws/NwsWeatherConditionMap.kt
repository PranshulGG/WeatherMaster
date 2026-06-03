package com.pranshulgg.weather_master_app.core.network.sources.weather.nws

import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition

object NwsWeatherConditionMap {

    /**
     * From https://api.weather.gov/icons
     * App doesn't support many conditions (for e.g. Windy, Tornado, Hurricane)
     * We can map them as "THUNDERSTORM" until supported
     * Example Icon Url: https://api.weather.gov/icons/land/day/skc?size=medium
     */
    fun getCondition(iconUrl: String?): WeatherCondition {
        val icon = stripUrlAndGetIconId(iconUrl) ?: "error"

        return when (icon) {
            "skc" -> WeatherCondition.CLEAR_SKY
            "few" -> WeatherCondition.MOSTLY_CLEAR
            "sct" -> WeatherCondition.PARTLY_CLOUDY
            "bkn" -> WeatherCondition.PARTLY_CLOUDY
            "ovc" -> WeatherCondition.OVERCAST
            "wind_skc" -> WeatherCondition.CLEAR_SKY
            "wind_few" -> WeatherCondition.MOSTLY_CLEAR
            "wind_sct" -> WeatherCondition.PARTLY_CLOUDY
            "wind_bkn" -> WeatherCondition.PARTLY_CLOUDY
            "wind_ovc" -> WeatherCondition.OVERCAST
            "snow" -> WeatherCondition.SNOW
            "rain_snow" -> WeatherCondition.LIGHT_SNOW
            "rain_sleet" -> WeatherCondition.LIGHT_SNOW
            "snow_sleet" -> WeatherCondition.LIGHT_SNOW
            "fzra" -> WeatherCondition.RAIN
            "rain_fzra" -> WeatherCondition.RAIN
            "snow_fzra" -> WeatherCondition.SNOW
            "sleet" -> WeatherCondition.SNOW
            "rain" -> WeatherCondition.RAIN
            "rain_showers" -> WeatherCondition.RAIN
            "rain_showers_hi" -> WeatherCondition.RAIN
            "tsra" -> WeatherCondition.THUNDERSTORM
            "tsra_sct" -> WeatherCondition.THUNDERSTORM
            "tsra_hi" -> WeatherCondition.THUNDERSTORM
            "tornado" -> WeatherCondition.THUNDERSTORM // TODO: FIX THIS
            "hurricane" -> WeatherCondition.THUNDERSTORM // TODO: FIX THIS
            "tropical_storm" -> WeatherCondition.THUNDERSTORM // TODO: FIX THIS
            "dust" -> WeatherCondition.FOG_HAZE
            "smoke" -> WeatherCondition.FOG_HAZE
            "haze" -> WeatherCondition.FOG_HAZE
            "hot" -> WeatherCondition.VERY_HOT
            "cold" -> WeatherCondition.VERY_COLD
            "blizzard" -> WeatherCondition.HEAVY_SNOW
            "fog" -> WeatherCondition.FOG_HAZE
            else -> WeatherCondition.NO_CONDITION_FOUND
        }
    }


    private fun stripUrlAndGetIconId(iconUrl: String?): String? {

        val id = iconUrl?.substringAfterLast("/")?.substringBefore("?")?.substringBefore(",")

        return id
    }

}