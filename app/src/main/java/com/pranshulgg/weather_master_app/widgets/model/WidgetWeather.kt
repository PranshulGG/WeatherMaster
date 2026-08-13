package com.pranshulgg.weather_master_app.widgets.model

import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherCurrent
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.model.weather.uv.UvIndex
import kotlinx.serialization.Serializable

@Serializable
data class WidgetWeather(
    val currentTemp: String,
    val currentCondition: String,
    val currentIcon: Int,
    val currentFrog: Int,
    val hourly: List<WidgetHourlyItem>,
    val daily: List<WidgetDailyItem>,
    val locationName: String,
    val summary: String,
    val weatherCondition: WeatherCondition,
    val uvIndex: Int? = null
)

@Serializable
data class WidgetHourlyItem(
    val temp: String,
    val conditionIcon: Int,
    val time: String,
    val precipitationProbability: Int? = null,
)

@Serializable
data class WidgetDailyItem(
    val tempMax: String,
    val tempMin: String,
    val conditionIcon: Int,
    val time: String,
    val conditionName: String,
    val maxUvIndex: Int? = null,
    val maxUvIndexAt: String? = null
)