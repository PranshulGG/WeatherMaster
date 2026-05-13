package com.pranshulgg.weathermaster.core.utils.weather.airquality

import androidx.compose.ui.graphics.Color
import com.pranshulgg.weathermaster.core.model.weather.airquality.AirQualityLevel

object AirQualityColors {
    fun getColors(level: AirQualityLevel): Color {
        return when (level) {
            AirQualityLevel.GOOD -> Color(0xFF00FF00)
            AirQualityLevel.FAIR -> Color(0xFF66FF00)
            AirQualityLevel.MODERATE -> Color(0xFFFFFF00)
            AirQualityLevel.POOR -> Color(0xFFFFA500)
            AirQualityLevel.VERY_POOR -> Color(0xFFFF0000)
            else -> Color(0xFF800080)
        }
    }
}