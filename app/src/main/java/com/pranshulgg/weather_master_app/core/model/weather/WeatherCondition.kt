package com.pranshulgg.weather_master_app.core.model.weather

import android.content.Context
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily


// TODO: ADD SLEET, HAIL, WINDY, BLIZZARD, TORNADO, HURRICANE
enum class WeatherCondition {
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

    NO_CONDITION_FOUND,

    // USED FOR DAILY
    CLEAR_WITH_CLOUDY,
    CLEAR_WITH_RAIN,
    CLEAR_WITH_SNOW,

    CLOUDY_WITH_CLEAR,
    CLOUDY_WITH_RAIN,
    CLOUDY_WITH_SNOW,

    RAIN_WITH_CLEAR,
    RAIN_WITH_CLOUDY,
    RAIN_WITH_SNOW,

    SNOW_WITH_CLEAR,
    SNOW_WITH_CLOUDY,
    SNOW_WITH_RAIN,

    CLEAR_THEN_CLOUDY,
    CLEAR_THEN_RAIN,
    CLEAR_THEN_SNOW,

    CLOUDY_THEN_CLEAR,
    CLOUDY_THEN_RAIN,
    CLOUDY_THEN_SNOW,

    RAIN_THEN_CLEAR,
    RAIN_THEN_CLOUDY,
    RAIN_THEN_SNOW,

    SNOW_THEN_CLEAR,
    SNOW_THEN_CLOUDY,
    SNOW_THEN_RAIN

}


// ------- MAP TO ICONS -------

fun WeatherCondition.toIcon(daily: WeatherDaily? = null, targetTimeMilli: Long): Int {
    val isDay = if (daily != null) {
        targetTimeMilli in daily.sunrise..daily.sunset
    } else {
        true
    }

    return when (this) {
        WeatherCondition.CLEAR_SKY -> if (isDay) R.drawable.weather_clear_day else R.drawable.weather_clear_night

        WeatherCondition.OVERCAST -> R.drawable.weather_overcast

        WeatherCondition.PARTLY_CLOUDY -> if (isDay) R.drawable.weather_partly_cloudy_day else R.drawable.weather_partly_cloudy_night

        WeatherCondition.HEAVY_RAIN -> R.drawable.weather_heavy_rain
        WeatherCondition.LIGHT_RAIN -> if (isDay) R.drawable.weather_light_rain_day else R.drawable.weather_light_rain_night

        WeatherCondition.LIGHT_SNOW -> if (isDay) R.drawable.weather_light_snow_day else R.drawable.weather_light_snow_night
        WeatherCondition.SNOW -> R.drawable.weather_snow
        WeatherCondition.HEAVY_SNOW -> R.drawable.weather_heavy_snow


        WeatherCondition.MOSTLY_CLEAR -> if (isDay) R.drawable.weather_mostly_clear_day else R.drawable.weather_mostly_clear_night


        WeatherCondition.THUNDERSTORM -> R.drawable.weather_thunderstorm

        WeatherCondition.VERY_HOT -> R.drawable.weather_very_hot
        WeatherCondition.VERY_COLD -> R.drawable.weather_very_cold

        WeatherCondition.FOG_HAZE -> R.drawable.weather_haze_fog_dust_smoke

        WeatherCondition.RAIN -> R.drawable.weather_rain

        WeatherCondition.CLEAR_WITH_CLOUDY -> R.drawable.clear_with_cloudy
        WeatherCondition.CLEAR_WITH_RAIN -> R.drawable.clear_with_rain
        WeatherCondition.CLEAR_WITH_SNOW -> R.drawable.clear_with_snow

        WeatherCondition.CLOUDY_WITH_CLEAR -> R.drawable.cloudy_with_clear
        WeatherCondition.CLOUDY_WITH_RAIN -> R.drawable.cloudy_with_rain
        WeatherCondition.CLOUDY_WITH_SNOW -> R.drawable.cloudy_with_snow

        WeatherCondition.RAIN_WITH_CLEAR -> R.drawable.rain_with_clear
        WeatherCondition.RAIN_WITH_CLOUDY -> R.drawable.rain_with_cloudy
        WeatherCondition.RAIN_WITH_SNOW -> R.drawable.rain_with_snow

        WeatherCondition.SNOW_WITH_CLEAR -> R.drawable.snow_with_clear
        WeatherCondition.SNOW_WITH_CLOUDY -> R.drawable.snow_with_cloudy
        WeatherCondition.SNOW_WITH_RAIN -> R.drawable.snow_with_rain

        WeatherCondition.CLEAR_THEN_CLOUDY -> R.drawable.clear_then_cloudy
        WeatherCondition.CLEAR_THEN_RAIN -> R.drawable.clear_then_rain
        WeatherCondition.CLEAR_THEN_SNOW -> R.drawable.clear_then_snow

        WeatherCondition.CLOUDY_THEN_CLEAR -> R.drawable.cloudy_then_clear
        WeatherCondition.CLOUDY_THEN_RAIN -> R.drawable.cloudy_then_rain
        WeatherCondition.CLOUDY_THEN_SNOW -> R.drawable.cloudy_then_snow

        WeatherCondition.RAIN_THEN_CLEAR -> R.drawable.rain_then_clear
        WeatherCondition.RAIN_THEN_CLOUDY -> R.drawable.rain_then_cloudy
        WeatherCondition.RAIN_THEN_SNOW -> R.drawable.rain_then_snow

        WeatherCondition.SNOW_THEN_CLEAR -> R.drawable.snow_then_clear
        WeatherCondition.SNOW_THEN_CLOUDY -> R.drawable.snow_then_cloudy
        WeatherCondition.SNOW_THEN_RAIN -> R.drawable.snow_then_rain

        WeatherCondition.NO_CONDITION_FOUND -> R.drawable.weather_not_available


    }

}


// ------- MAP TO LABEL -------


fun WeatherCondition.toLabel(context: Context): String {

    return when (this) {
        WeatherCondition.CLEAR_SKY -> context.getString(R.string.weather_clear_sky)

        WeatherCondition.OVERCAST -> context.getString(R.string.weather_overcast)

        WeatherCondition.PARTLY_CLOUDY -> context.getString(R.string.weather_partly_cloudy)

        WeatherCondition.HEAVY_RAIN -> context.getString(R.string.weather_heavy_rain)
        WeatherCondition.LIGHT_RAIN -> context.getString(R.string.weather_light_rain)

        WeatherCondition.LIGHT_SNOW -> context.getString(R.string.weather_light_snow)
        WeatherCondition.SNOW -> context.getString(R.string.weather_snow)
        WeatherCondition.HEAVY_SNOW -> context.getString(R.string.weather_heavy_snow)


        WeatherCondition.MOSTLY_CLEAR -> context.getString(R.string.weather_mostly_clear)


        WeatherCondition.THUNDERSTORM -> context.getString(R.string.weather_thunderstorm)

        WeatherCondition.VERY_HOT -> context.getString(R.string.weather_very_hot)
        WeatherCondition.VERY_COLD -> context.getString(R.string.weather_very_cold)

        WeatherCondition.FOG_HAZE -> context.getString(R.string.weather_haze)

        WeatherCondition.RAIN -> context.getString(R.string.weather_rain)

        WeatherCondition.CLEAR_WITH_CLOUDY -> context.getString(R.string.weather_clear_with_cloudy)
        WeatherCondition.CLEAR_WITH_RAIN -> context.getString(R.string.weather_clear_with_rain)
        WeatherCondition.CLEAR_WITH_SNOW -> context.getString(R.string.weather_clear_with_snow)

        WeatherCondition.CLOUDY_WITH_CLEAR -> context.getString(R.string.weather_cloudy_with_clear)
        WeatherCondition.CLOUDY_WITH_RAIN -> context.getString(R.string.weather_cloudy_with_rain)
        WeatherCondition.CLOUDY_WITH_SNOW -> context.getString(R.string.weather_cloudy_with_snow)

        WeatherCondition.RAIN_WITH_CLEAR -> context.getString(R.string.weather_rain_with_clear)
        WeatherCondition.RAIN_WITH_CLOUDY -> context.getString(R.string.weather_rain_with_cloudy)
        WeatherCondition.RAIN_WITH_SNOW -> context.getString(R.string.weather_rain_with_snow)

        WeatherCondition.SNOW_WITH_CLEAR -> context.getString(R.string.weather_snow_with_clear)
        WeatherCondition.SNOW_WITH_CLOUDY -> context.getString(R.string.weather_snow_with_cloudy)
        WeatherCondition.SNOW_WITH_RAIN -> context.getString(R.string.weather_snow_with_rain)

        WeatherCondition.CLEAR_THEN_CLOUDY -> context.getString(R.string.weather_clear_then_cloudy)
        WeatherCondition.CLEAR_THEN_RAIN -> context.getString(R.string.weather_clear_then_rain)
        WeatherCondition.CLEAR_THEN_SNOW -> context.getString(R.string.weather_clear_then_snow)

        WeatherCondition.CLOUDY_THEN_CLEAR -> context.getString(R.string.weather_cloudy_then_clear)
        WeatherCondition.CLOUDY_THEN_RAIN -> context.getString(R.string.weather_cloudy_then_rain)
        WeatherCondition.CLOUDY_THEN_SNOW -> context.getString(R.string.weather_cloudy_then_snow)

        WeatherCondition.RAIN_THEN_CLEAR -> context.getString(R.string.weather_rain_then_clear)
        WeatherCondition.RAIN_THEN_CLOUDY -> context.getString(R.string.weather_rain_then_cloudy)
        WeatherCondition.RAIN_THEN_SNOW -> context.getString(R.string.weather_rain_then_snow)

        WeatherCondition.SNOW_THEN_CLEAR -> context.getString(R.string.weather_snow_then_clear)
        WeatherCondition.SNOW_THEN_CLOUDY -> context.getString(R.string.weather_snow_then_cloudy)
        WeatherCondition.SNOW_THEN_RAIN -> context.getString(R.string.weather_snow_then_rain)

        WeatherCondition.NO_CONDITION_FOUND -> context.getString(R.string.weather_no_condition)


    }

}

// ------- MAP TO FROGGYY -------

fun WeatherCondition.toFroggy(daily: WeatherDaily? = null, targetTimeMilli: Long): Int {

    val isDay = if (daily != null) {
        targetTimeMilli in daily.sunrise..daily.sunset
    } else {
        true
    }

    return when (this) {
        WeatherCondition.CLEAR_SKY -> if (isDay) R.drawable.froggy_clear_day else R.drawable.froggy_clear_night

        WeatherCondition.OVERCAST -> R.drawable.froggy_overcast

        WeatherCondition.PARTLY_CLOUDY -> R.drawable.froggy_partly_cloudy

        WeatherCondition.HEAVY_RAIN -> R.drawable.froggy_rain
        WeatherCondition.LIGHT_RAIN -> R.drawable.froggy_light_rain

        WeatherCondition.LIGHT_SNOW -> R.drawable.froggy_snow
        WeatherCondition.SNOW -> R.drawable.froggy_snow
        WeatherCondition.HEAVY_SNOW -> R.drawable.froggy_snow


        WeatherCondition.MOSTLY_CLEAR -> if (isDay) R.drawable.froggy_mostly_clear_day else R.drawable.froggy_mostly_clear_night


        WeatherCondition.THUNDERSTORM -> R.drawable.froggy_thunder

        WeatherCondition.VERY_HOT -> R.drawable.weather_not_available // WILL NEVER HAPPEN IN FROGGIES CASE
        WeatherCondition.VERY_COLD -> R.drawable.weather_not_available // WILL NEVER HAPPEN IN FROGGIES CASE

        WeatherCondition.FOG_HAZE -> R.drawable.froggy_haze

        WeatherCondition.RAIN -> R.drawable.froggy_rain

        WeatherCondition.NO_CONDITION_FOUND -> R.drawable.weather_not_available

        else -> R.drawable.weather_not_available

    }

}
