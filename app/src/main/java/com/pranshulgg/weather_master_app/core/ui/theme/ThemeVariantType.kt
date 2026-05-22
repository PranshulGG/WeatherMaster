package com.pranshulgg.weather_master_app.core.ui.theme

import com.materialkolor.PaletteStyle

enum class ThemeVariantType {
    TONAL_SPOT,
    NEUTRAL,
    VIBRANT,
    EXPRESSIVE
}

fun ThemeVariantType.toPaletteStyle(): PaletteStyle {
    return when (this) {
        ThemeVariantType.TONAL_SPOT -> PaletteStyle.TonalSpot
        ThemeVariantType.NEUTRAL -> PaletteStyle.Neutral
        ThemeVariantType.VIBRANT -> PaletteStyle.Vibrant
        ThemeVariantType.EXPRESSIVE -> PaletteStyle.Expressive
    }
}