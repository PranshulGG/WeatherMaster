package com.pranshulgg.weather_master_app.feature.blocks

import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits


data class BlockScreenUiState(
    val weather: Weather? = null,
    val units: WeatherUnits = WeatherUnits.getDefault()
)
