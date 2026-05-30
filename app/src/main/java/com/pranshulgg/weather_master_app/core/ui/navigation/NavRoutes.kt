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

    const val ABOUT = "about"

    const val TERMS_CONDITIONS = "terms_conditions"
    const val PRIVACY_POLICY = "privacy_policy"
    const val LICENSE = "LICENSE"

    const val SOURCES = "sources"

    const val UV_INDEX = "uv_index"
    const val HUMIDITY = "humidity"
    const val VISIBILITY = "visibility"
    const val SUN_MOON = "sun_moon"
    const val PRESSURE = "pressure"

    fun blockScreen(block: String, index: Int, locationId: String): String {
        return "$block/$index/$locationId"
    }
}