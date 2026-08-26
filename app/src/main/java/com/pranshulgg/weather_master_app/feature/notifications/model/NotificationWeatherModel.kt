package com.pranshulgg.weather_master_app.feature.notifications.model

import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition


data class NotificationWeatherModel(
    val current: NotificationCurrentWeather,
    val daily: List<NotificationDailyWeather>,
    val hourly: List<NotificationHourlyWeather>
)

data class NotificationCurrentWeather(
    val temp: String?,
    val feelsLike: String?,
    val uvIndex: String?,
    val currentCondition: String,
    val currentConditionIcon: Int,
)

data class NotificationDailyWeather(
    val summary: String?,
    val maxTemp: String?,
    val minTemp: String?,
    val condition: String,
    val conditionIcon: Int,
    val pop: String?,
)

data class NotificationHourlyWeather(
    val time: String?,
    val temp: String?,
    val condition: String,
    val conditionIcon: Int,
    val pop: String?,
)