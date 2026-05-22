package com.pranshulgg.weather_master_app.core.model.weather.uv

import android.content.Context
import com.pranshulgg.weather_master_app.R

enum class UvIndex {
    LOW,
    MODERATE,
    HIGH,
    VERY_HIGH,
    EXTREME
}

fun getUvIndex(uvIndex: Int): UvIndex {
    return when (uvIndex) {
        in 0..3 -> UvIndex.LOW
        in 3..6 -> UvIndex.MODERATE
        in 6..8 -> UvIndex.HIGH
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