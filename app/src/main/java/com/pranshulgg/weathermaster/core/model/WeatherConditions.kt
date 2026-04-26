package com.pranshulgg.weathermaster.core.model

import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.model.domain.WeatherDaily

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

fun WeatherConditions.toIcon(daily: WeatherDaily? = null, targetTimeSecs: Long): Int {

    val isDay = if (daily != null) {
        targetTimeSecs in daily.sunrise..daily.sunset
    } else {
        true
    }

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


// ------- MAP TO LABEL -------


fun WeatherConditions.toLabel(): String {

    return when (this) {
        WeatherConditions.CLEAR_SKY -> "Clear sky"

        WeatherConditions.OVERCAST -> "Overcast"

        WeatherConditions.PARTLY_CLOUDY -> "Partly cloudy"

        WeatherConditions.HEAVY_RAIN -> "Heavy rain"
        WeatherConditions.LIGHT_RAIN -> "Light rain"

        WeatherConditions.LIGHT_SNOW -> "Light snow"
        WeatherConditions.SNOW -> "Snowy"
        WeatherConditions.HEAVY_SNOW -> "Heavy snow"


        WeatherConditions.MOSTLY_CLEAR -> "Mostly clear"


        WeatherConditions.THUNDERSTORM -> "Thunderstorm"

        WeatherConditions.VERY_HOT -> "Very hot"
        WeatherConditions.VERY_COLD -> "Very cold"

        WeatherConditions.FOG_HAZE -> "Haze"

        WeatherConditions.RAIN -> "Rainy"

        WeatherConditions.NO_CONDITION_FOUND -> "No condition found"


    }

}

// ------- MAP TO FROGGYY -------

fun WeatherConditions.toFroggy(daily: WeatherDaily? = null, targetTimeSecs: Long): Int {

    val isDay = if (daily != null) {
        targetTimeSecs in daily.sunrise..daily.sunset
    } else {
        true
    }

    return when (this) {
        WeatherConditions.CLEAR_SKY -> if (isDay) R.drawable.froggy_clear_day else R.drawable.froggy_clear_night

        WeatherConditions.OVERCAST -> R.drawable.froggy_overcast

        WeatherConditions.PARTLY_CLOUDY -> R.drawable.froggy_partly_cloudy

        WeatherConditions.HEAVY_RAIN -> R.drawable.froggy_rain
        WeatherConditions.LIGHT_RAIN -> R.drawable.froggy_light_rain

        WeatherConditions.LIGHT_SNOW -> R.drawable.froggy_snow
        WeatherConditions.SNOW -> R.drawable.froggy_snow
        WeatherConditions.HEAVY_SNOW -> R.drawable.froggy_snow


        WeatherConditions.MOSTLY_CLEAR -> if (isDay) R.drawable.froggy_mostly_clear_day else R.drawable.froggy_mostly_clear_night


        WeatherConditions.THUNDERSTORM -> R.drawable.froggy_thunder

        WeatherConditions.VERY_HOT -> R.drawable.weather_not_available // WILL NEVER HAPPEN IN FROGGIES CASE
        WeatherConditions.VERY_COLD -> R.drawable.weather_not_available // WILL NEVER HAPPEN IN FROGGIES CASE

        WeatherConditions.FOG_HAZE -> R.drawable.froggy_haze

        WeatherConditions.RAIN -> R.drawable.froggy_rain

        WeatherConditions.NO_CONDITION_FOUND -> R.drawable.weather_not_available


    }

}
