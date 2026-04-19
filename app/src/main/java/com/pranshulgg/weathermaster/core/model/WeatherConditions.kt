package com.pranshulgg.weathermaster.core.model

import com.pranshulgg.weathermaster.R

enum class WeatherConditions {
    CLEAR_SKY,
    OVERCAST,
    PARTLY_CLOUDY,
    HEAVY_RAIN,
    HEAVY_SNOW,
    LIGHT_SNOW,
    LIGHT_RAIN,
    MOSTLY_CLEAR,
    SNOW,
    THUNDERSTORM,
    VERY_HOT,
    VERY_COLD,
    FOG_HAZE,

    RAIN,

    NO_CONDITION_FOUND

}


// ------- MAP TO ICONS -------

fun WeatherConditions.toIcon(isDay: Boolean): Int {

    return when (this) {
        WeatherConditions.CLEAR_SKY -> if (isDay) R.drawable.weather_clear_day else R.drawable.weather_clear_night

        WeatherConditions.OVERCAST -> R.drawable.weather_overcast

        WeatherConditions.PARTLY_CLOUDY -> if (isDay) R.drawable.weather_partly_cloudy_day else R.drawable.weather_partly_cloudy_night

        WeatherConditions.HEAVY_RAIN -> R.drawable.weather_heavy_rain
        WeatherConditions.LIGHT_RAIN -> if (isDay) R.drawable.weather_light_rain_day else R.drawable.weather_light_rain_night

        WeatherConditions.LIGHT_SNOW -> if (isDay) R.drawable.weather_light_snow_day else R.drawable.weather_light_snow_night
        WeatherConditions.SNOW -> R.drawable.weather_snow
        WeatherConditions.HEAVY_SNOW -> R.drawable.weather_heavy_snow


        WeatherConditions.MOSTLY_CLEAR -> if (isDay) R.drawable.weather_mostly_clear_day else R.drawable.weather_mostly_clear_night


        WeatherConditions.THUNDERSTORM -> R.drawable.weather_thunderstorm

        WeatherConditions.VERY_HOT -> R.drawable.weather_very_hot
        WeatherConditions.VERY_COLD -> R.drawable.weather_very_cold

        WeatherConditions.FOG_HAZE -> R.drawable.weather_haze_fog_dust_smoke

        WeatherConditions.RAIN -> R.drawable.weather_rain

        WeatherConditions.NO_CONDITION_FOUND -> R.drawable.weather_not_available


    }

}