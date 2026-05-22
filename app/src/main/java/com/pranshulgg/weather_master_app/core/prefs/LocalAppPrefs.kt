package com.pranshulgg.weather_master_app.core.prefs

import androidx.compose.runtime.staticCompositionLocalOf

val LocalAppPrefs = staticCompositionLocalOf<AppPrefsState> {
    error("AppPrefsState not provided")
}