package com.pranshulgg.weathermaster.core.model

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

fun UvIndex.toLabel(): String {
    return when (this) {
        UvIndex.LOW -> "Low"
        UvIndex.MODERATE -> "Moderate"
        UvIndex.HIGH -> "High"
        UvIndex.VERY_HIGH -> "Very High"
        UvIndex.EXTREME -> "Extreme"
    }
}