package com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json

data class OpenWeatherAirQualityJson(
    val list: List<OpenWeatherAirQualityItemJson>
)


data class OpenWeatherAirQualityItemJson(
    val dt: Long,
    val components: OpenWeatherAirQualityItemComponentsJson
)


data class OpenWeatherAirQualityItemComponentsJson(
    val co: Double?,
    val no: Double?,
    val no2: Double?,
    val o3: Double?,
    val so2: Double?,
    val pm2_5: Double?,
    val pm10: Double?,
    val nh3: Double?
)