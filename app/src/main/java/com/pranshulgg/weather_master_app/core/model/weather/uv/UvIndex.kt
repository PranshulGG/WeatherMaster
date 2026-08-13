package com.pranshulgg.weather_master_app.core.model.weather.uv

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityLevel

enum class UvIndex {
    LOW,
    MODERATE,
    HIGH,
    VERY_HIGH,
    EXTREME
}

fun getUvIndex(uvIndex: Int): UvIndex {
    return when (uvIndex) {
        in 0..2 -> UvIndex.LOW
        in 3..5 -> UvIndex.MODERATE
        in 6..7 -> UvIndex.HIGH
        in 8..10 -> UvIndex.VERY_HIGH
        else -> UvIndex.EXTREME
    }
}

fun UvIndex.toLabel(context: Context): String {
    return when (this) {
        UvIndex.LOW -> context.getString(R.string.weather_uv_low)
        UvIndex.MODERATE -> context.getString(R.string.weather_uv_moderate)
        UvIndex.HIGH -> context.getString(R.string.weather_uv_high)
        UvIndex.VERY_HIGH -> context.getString(R.string.weather_uv_very_high)
        UvIndex.EXTREME -> context.getString(R.string.weather_uv_extreme)
    }
}

fun UvIndex.toColor(): Color {
    return when (this) {
        UvIndex.LOW -> Color(0xFF4CAF50)
        UvIndex.MODERATE -> Color(0xFFFFC107)
        UvIndex.HIGH -> Color(0xFFFF9800)
        UvIndex.VERY_HIGH -> Color(0xFFF44336)
        UvIndex.EXTREME -> Color(0xFF9C27B0)
    }
}

fun UvIndex.toTextColor(): Color {
    return when (this) {
        UvIndex.LOW -> Color(0xFF0F3D0F)
        UvIndex.MODERATE -> Color(0xFF5C4300)
        UvIndex.HIGH -> Color(0xFFFFF4E0)
        UvIndex.VERY_HIGH -> Color(0xFFFFEBEE)
        UvIndex.EXTREME -> Color(0xFFFFE4F1)
    }
}