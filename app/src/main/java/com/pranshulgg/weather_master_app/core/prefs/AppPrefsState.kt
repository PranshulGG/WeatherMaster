package com.pranshulgg.weather_master_app.core.prefs

import com.google.gson.annotations.Until
import com.pranshulgg.weather_master_app.core.model.sources.SearchSource
import com.pranshulgg.weather_master_app.core.ui.theme.ThemeVariantType


data class AppPrefsState(
    val appTheme: String,
    val setAppTheme: (String) -> Unit,

    val customThemeColor: String,
    val setCustomThemeColor: (String) -> Unit,

    val isCustomTheme: Boolean,
    val setUseCustomTheme: (Boolean) -> Unit,

    val isDynamicTheme: Boolean,
    val setDynamicColor: (Boolean) -> Unit,

    val themeVariantType: ThemeVariantType,
    val setThemeVariantType: (ThemeVariantType) -> Unit,

    val searchSource: SearchSource,
    val setSearchSource: (SearchSource) -> Unit,

    val backgroundUpdatesEnabled: Boolean,
    val setBackgroundUpdates: (Boolean) -> Unit,

    val backgroundUpdatesInterval: Int,
    val setBackgroundUpdatesInterval: (Int) -> Unit,

    val isFroggyLayout: Boolean,
    val setFroggyLayout: (Boolean) -> Unit,

    val isShowWeatherAnimations: Boolean,
    val setShowWeatherAnimations: (Boolean) -> Unit,

    val isWeatherBasedTheme: Boolean,
    val setIsWeatherBasedTheme: (Boolean) -> Unit,

    val is24HrTimeFormat: Boolean,
    val set24HrTimeFormat: (Boolean) -> Unit,

    val isShowSummary: Boolean,
    val setShowSummary: (Boolean) -> Unit,

    val isGoogleSansFlex: Boolean,
    val setGoogleSansFlex: (Boolean) -> Unit
)


