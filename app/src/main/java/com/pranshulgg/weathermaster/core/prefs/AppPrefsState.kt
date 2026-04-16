package com.pranshulgg.weathermaster.core.prefs


data class AppPrefsState(
    val appTheme: String,
    val setAppTheme: (String) -> Unit,

    val customThemeColor: String,
    val setCustomThemeColor: (String) -> Unit,

    val isCustomTheme: Boolean,
    val setCustomTheme: (Boolean) -> Unit,

    val isDynamicTheme: Boolean,
    val setDynamicColor: (Boolean) -> Unit
)