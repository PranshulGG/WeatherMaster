package com.pranshulgg.weather_master_app.widgets.ui.colors

import androidx.glance.unit.ColorProvider
import com.pranshulgg.weather_master_app.R


enum class WidgetTextTheme(val label: Int) {
    WHITE(R.string.setting_light_theme),
    BLACK(R.string.setting_dark_theme),
    AUTO(R.string.setting_system_theme),
}

enum class WidgetTheme(val label: Int) {
    DARK(R.string.setting_dark_theme),
    LIGHT(R.string.setting_light_theme),
    AUTO(R.string.setting_system_theme),
    TRANSPARENT(R.string.color_transparent)
}


class WidgetColors {

    fun getTextColor(theme: WidgetTextTheme, widgetTheme: WidgetTheme): Pair<ColorProvider, Int>? {

        val color = when (theme) {
            WidgetTextTheme.WHITE -> R.color.white
            WidgetTextTheme.BLACK -> R.color.black
            WidgetTextTheme.AUTO -> if (widgetTheme == WidgetTheme.TRANSPARENT)
                R.color.white else if (widgetTheme == WidgetTheme.DARK)
                R.color.white else if (widgetTheme == WidgetTheme.LIGHT)
                R.color.black else null
        }

        return if (color == null) null else Pair(ColorProvider(color), color)
    }

    fun getTextVariantColor(theme: WidgetTextTheme, widgetTheme: WidgetTheme): ColorProvider? {

        val color = when (theme) {
            WidgetTextTheme.WHITE -> R.color.white_secondary
            WidgetTextTheme.BLACK -> R.color.black_secondary
            WidgetTextTheme.AUTO -> if (widgetTheme == WidgetTheme.TRANSPARENT)
                R.color.white_secondary else if (widgetTheme == WidgetTheme.DARK)
                R.color.white_secondary else if (widgetTheme == WidgetTheme.LIGHT)
                R.color.black_secondary else null
        }

        return if (color == null) null else ColorProvider(color)
    }

    fun getBackgroundColor(widgetTheme: WidgetTheme): ColorProvider? {
        val color = when (widgetTheme) {
            WidgetTheme.LIGHT -> R.color.white
            WidgetTheme.DARK -> R.color.black
            WidgetTheme.TRANSPARENT -> R.color.transparent
            WidgetTheme.AUTO -> null
        }

        return if (color == null) null else ColorProvider(color)
    }

}

