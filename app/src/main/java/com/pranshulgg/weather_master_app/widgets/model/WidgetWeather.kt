package com.pranshulgg.weather_master_app.widgets.model

import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import kotlinx.serialization.Serializable

@Serializable
data class WidgetWeather(
    val currentTemp: String,
    val currentCondition: String,
    val currentIcon: Int,
    val currentFrog: Int,
    val hourly: List<WidgetHourlyItem>,
    val daily: List<WidgetDailyItem>,
    val uvIndex: Int?
)

@Serializable
data class WidgetHourlyItem(
    val temp: String,
    val conditionIcon: Int,
    val time: String
)

@Serializable
data class WidgetDailyItem(
    val tempMax: String,
    val tempMin: String,
    val conditionIcon: Int,
    val time: String
)