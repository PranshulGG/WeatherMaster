package com.pranshulgg.weather_master_app.core.model.sources


enum class AlertSource(
    val displayName: String,
    val displayLink: String,
    val fullName: String

) {
    NONE(displayName = "None", fullName = "", displayLink = ""),
    ACCU_WEATHER(
        displayName = "AccuWeather",
        fullName = "AccuWeather",
        displayLink = "https://www.accuweather.com/"
    ),
    WEATHER_API(
        displayName = "WeatherApi",
        fullName = "WeatherApi.com",
        displayLink = "https://www.weatherapi.com/"
    ),
    WMO_SEVERE_WEATHER(
        displayName = "WMO Severe Weather",
        fullName = "WMO Severe Weather Information Centre",
        displayLink = "https://www.wmo.int/"
    )

}