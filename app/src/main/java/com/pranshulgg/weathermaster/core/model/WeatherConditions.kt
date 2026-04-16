package com.pranshulgg.weathermaster.core.model

import com.pranshulgg.weathermaster.R

enum class WeatherConditions {
    CLEAR_DAY,
    OVERCAST,
    PARTLY_CLOUDY_DAY,
    HEAVY_RAIN,
    HEAVY_SNOW,
    LIGHT_SNOW_DAY,
    LIGHT_RAIN_DAY,
    MOSTLY_CLEAR_DAY,
    SNOW,
    THUNDERSTORM,
    VERY_HOT,
    VERY_COLD,
    FOG_HAZE,

// ------- NIGHT -------

    CLEAR_NIGHT,
    LIGHT_SNOW_NIGHT,
    LIGHT_RAIN_NIGHT,
    MOSTLY_CLEAR_NIGHT,
    PARTLY_CLOUDY_NIGHT
}


// ------- MAPPING -------

fun WeatherConditions.toIcon(): Int {

    return when (this) {
        WeatherConditions.CLEAR_DAY -> R.drawable.weather_clear_day
        WeatherConditions.CLEAR_NIGHT -> R.drawable.weather_clear_night

        WeatherConditions.OVERCAST -> R.drawable.weather_overcast

        WeatherConditions.PARTLY_CLOUDY_DAY -> R.drawable.weather_partly_cloudy_day
        WeatherConditions.PARTLY_CLOUDY_NIGHT -> R.drawable.weather_partly_cloudy_night

        WeatherConditions.HEAVY_RAIN -> R.drawable.weather_heavy_rain
        WeatherConditions.LIGHT_RAIN_DAY -> R.drawable.weather_light_rain_day
        WeatherConditions.LIGHT_RAIN_NIGHT -> R.drawable.weather_light_rain_night

        WeatherConditions.LIGHT_SNOW_DAY -> R.drawable.weather_light_snow_day
        WeatherConditions.LIGHT_SNOW_NIGHT -> R.drawable.weather_light_snow_night
        WeatherConditions.SNOW -> R.drawable.weather_snow
        WeatherConditions.HEAVY_SNOW -> R.drawable.weather_heavy_snow


        WeatherConditions.MOSTLY_CLEAR_DAY -> R.drawable.weather_mostly_clear_day
        WeatherConditions.MOSTLY_CLEAR_NIGHT -> R.drawable.weather_mostly_clear_night

        WeatherConditions.THUNDERSTORM -> R.drawable.weather_thunderstorm

        WeatherConditions.VERY_HOT -> R.drawable.weather_very_hot
        WeatherConditions.VERY_COLD -> R.drawable.weather_very_cold

        WeatherConditions.FOG_HAZE -> R.drawable.weather_haze_fog_dust_smoke
    }

}