package com.pranshulgg.weathermaster.core.prefs

import androidx.compose.runtime.staticCompositionLocalOf

val LocalAppPrefs = staticCompositionLocalOf<AppPrefsState>{
    error("AppPrefsState not provided")
}