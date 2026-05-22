package com.pranshulgg.weather_master_app.core.utils.weather.airquality

import androidx.compose.ui.graphics.Color
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityLevel

object AirQualityColors {
    fun getColors(level: AirQualityLevel): Color {
        return when (level) {
            AirQualityLevel.GOOD -> Color(0xFF00FF00)
            AirQualityLevel.FAIR -> Color(0xFFFFC107)
            AirQualityLevel.MODERATE -> Color(0xFFCE8500)
            AirQualityLevel.POOR -> Color(0xFFFF0000)
            AirQualityLevel.VERY_POOR -> Color(0xFFc41061)
            else -> Color(0xFF800080)
        }
    }
}