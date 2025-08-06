package com.pranshulgg.weather_master_app.util

import com.pranshulgg.weather_master_app.R

object WeatherIconMapper {

    private val iconMap = mapOf(
        // 0: Clear sky
        "0_1" to R.drawable.sunny,
        "0_0" to R.drawable.clear_night,

        // 1,2,3: Mainly clear, partly cloudy, overcast
        "1_1" to R.drawable.mostly_sunny,
        "1_0" to R.drawable.mostly_clear_night,
        "2_1" to R.drawable.partly_cloudy,
        "2_0" to R.drawable.partly_cloudy_night,
        "3_1" to R.drawable.cloudy,
        "3_0" to R.drawable.cloudy,

        // 45,48: Fog
        "45_1" to R.drawable.haze_fog_dust_smoke,
        "45_0" to R.drawable.haze_fog_dust_smoke,
        "48_1" to R.drawable.haze_fog_dust_smoke,
        "48_0" to R.drawable.haze_fog_dust_smoke,

        // 51,53,55: Drizzle
        "51_1" to R.drawable.drizzle,
        "51_0" to R.drawable.drizzle,
        "53_1" to R.drawable.drizzle,
        "53_0" to R.drawable.drizzle,
        "55_1" to R.drawable.drizzle,
        "55_0" to R.drawable.drizzle,

        // 56,57: Freezing Drizzle
        "56_1" to R.drawable.wintry_mix_rain_snow,
        "56_0" to R.drawable.wintry_mix_rain_snow,
        "57_1" to R.drawable.wintry_mix_rain_snow,
        "57_0" to R.drawable.wintry_mix_rain_snow,

        // 61,63,65: Rain
        "61_1" to R.drawable.scattered_showers_day,
        "61_0" to R.drawable.scattered_showers_night,
        "63_1" to R.drawable.showers_rain,
        "63_0" to R.drawable.showers_rain,
        "65_1" to R.drawable.heavy_rain,
        "65_0" to R.drawable.heavy_rain,

        // 66,67: Freezing Rain
        "66_1" to R.drawable.wintry_mix_rain_snow,
        "66_0" to R.drawable.wintry_mix_rain_snow,
        "67_1" to R.drawable.wintry_mix_rain_snow,
        "67_0" to R.drawable.wintry_mix_rain_snow,

        // 71,73,75: Snowfall
        "71_1" to R.drawable.snow_showers_snow,
        "71_0" to R.drawable.heavy_snow,
        "73_1" to R.drawable.heavy_snow,
        "73_0" to R.drawable.heavy_snow,
        "75_1" to R.drawable.heavy_snow,
        "75_0" to R.drawable.heavy_snow,

        // 77: Snow grains
        "77_1" to R.drawable.flurries,
        "77_0" to R.drawable.flurries,

        // 80,81,82: Rain showers
        "80_1" to R.drawable.scattered_showers_day,
        "80_0" to R.drawable.scattered_showers_night,
        "81_1" to R.drawable.showers_rain,
        "81_0" to R.drawable.showers_rain,
        "82_1" to R.drawable.heavy_rain,
        "82_0" to R.drawable.heavy_rain,

        // 85,86: Snow showers
        "85_1" to R.drawable.flurries,
        "85_0" to R.drawable.flurries,
        "86_1" to R.drawable.heavy_snow,
        "86_0" to R.drawable.heavy_snow,

        // 95: Thunderstorm (no hail)
        "95_1" to R.drawable.isolated_scattered_tstorms_day,
        "95_0" to R.drawable.isolated_scattered_tstorms_night,

        // 96,99: Thunderstorm with hail
        "96_1" to R.drawable.strong_tstorms,
        "96_0" to R.drawable.strong_tstorms,
        "99_1" to R.drawable.strong_tstorms,
        "99_0" to R.drawable.strong_tstorms
    )


    fun getIconResource(code: String?, isDay: String?): Int {
        val key = "${code ?: "-1"}_${isDay ?: "-1"}"
        return iconMap[key] ?: R.drawable.showers_rain // fallback
    }
}
