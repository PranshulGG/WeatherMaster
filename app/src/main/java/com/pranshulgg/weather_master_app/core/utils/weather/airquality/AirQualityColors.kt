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

    fun getTextColors(level: AirQualityLevel): Color {
        return when (level) {
            AirQualityLevel.GOOD -> Color(0xFF0F3D0F)
            AirQualityLevel.FAIR -> Color(0xFF5C4300)
            AirQualityLevel.MODERATE -> Color(0xFFFFF4E0)
            AirQualityLevel.POOR -> Color(0xFFFFEBEE)
            AirQualityLevel.VERY_POOR -> Color(0xFFFFE4F1)
            else -> Color(0xFFF3E5F5)
        }
    }
}