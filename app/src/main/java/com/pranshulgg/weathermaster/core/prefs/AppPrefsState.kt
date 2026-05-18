package com.pranshulgg.weathermaster.core.prefs

import com.pranshulgg.weathermaster.core.model.sources.SearchSource
import com.pranshulgg.weathermaster.core.ui.theme.ThemeVariantType


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
    val setSearchSource: (SearchSource) -> Unit

)


