package com.pranshulgg.weathermaster.core.model

import android.content.Context
import androidx.compose.ui.res.stringResource
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


fun WeatherConditions.toLabel(context: Context): String {

    return when (this) {
        WeatherConditions.CLEAR_SKY -> context.getString(R.string.weather_clear_sky)

        WeatherConditions.OVERCAST -> context.getString(R.string.weather_overcast)

        WeatherConditions.PARTLY_CLOUDY -> context.getString(R.string.weather_partly_cloudy)

        WeatherConditions.HEAVY_RAIN -> context.getString(R.string.weather_heavy_rain)
        WeatherConditions.LIGHT_RAIN -> context.getString(R.string.weather_light_rain)

        WeatherConditions.LIGHT_SNOW -> context.getString(R.string.weather_light_snow)
        WeatherConditions.SNOW -> context.getString(R.string.weather_snow)
        WeatherConditions.HEAVY_SNOW -> context.getString(R.string.weather_heavy_snow)


        WeatherConditions.MOSTLY_CLEAR -> context.getString(R.string.weather_mostly_clear)


        WeatherConditions.THUNDERSTORM -> context.getString(R.string.weather_thunderstorm)

        WeatherConditions.VERY_HOT -> context.getString(R.string.weather_very_hot)
        WeatherConditions.VERY_COLD -> context.getString(R.string.weather_very_cold)

        WeatherConditions.FOG_HAZE -> context.getString(R.string.weather_haze)

        WeatherConditions.RAIN -> context.getString(R.string.weather_rain)

        WeatherConditions.NO_CONDITION_FOUND -> context.getString(R.string.weather_no_condition)


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
