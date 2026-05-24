package com.pranshulgg.weather_master_app.core.ui.navigation

object NavRoutes {
    const val MAIN = "main"

    const val SEARCH = "search"

    const val SETTINGS = "settings"

    const val UNITS = "units"

    const val LANGUAGE = "language"
    const val APPEARANCE = "appearance"

    const val DAILY = "daily"

    fun daily(index: Int, locationId: String): String {
        return "$DAILY/$index/$locationId"
    }

    const val BACKGROUND_UPDATES = "background_updates"
}