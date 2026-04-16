package com.pranshulgg.weathermaster.feature.main.ui

import androidx.compose.ui.graphics.Color
import com.pranshulgg.weathermaster.core.model.WeatherConditions

fun backgroundGradients(condition: WeatherConditions): List<Color> {
    return when (condition) {
        WeatherConditions.CLEAR_DAY -> listOf(Color(0xFF000630), Color(0xFF004a77))
        else -> listOf(Color(0xFF000630), Color(0xFF447FB3))

    }
}