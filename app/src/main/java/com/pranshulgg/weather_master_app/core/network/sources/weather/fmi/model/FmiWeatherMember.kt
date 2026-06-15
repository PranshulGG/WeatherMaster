package com.pranshulgg.weather_master_app.core.network.sources.weather.fmi.model

data class FmiWeatherMember(
    val time: String?,
    val parameterName: String?,
    val parameterValue: String?
)


data class FmiWeather(
    val data: List<FmiWeatherMember>,
    val observation: List<FmiWeatherMember>?
)