package com.pranshulgg.weather_master_app.core.model.sources

enum class AirQualitySource(
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
    OPEN_METEO(
        displayName = "Open Meteo",
        fullName = "Open Meteo",
        displayLink = "https://open-meteo.com/"
    )

}