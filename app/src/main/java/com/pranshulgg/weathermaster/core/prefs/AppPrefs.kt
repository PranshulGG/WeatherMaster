package com.pranshulgg.weathermaster.core.prefs


import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import com.pranshulgg.weathermaster.core.model.providers.SearchProviders
import com.pranshulgg.weathermaster.core.ui.theme.ThemeVariantType
import com.pranshulgg.weathermaster.core.prefs.helper.PreferencesHelper

object AppPrefs {
    private val _appTheme = mutableStateOf("Dark")
    private val _customThemeColor = mutableStateOf("#2196f3")
    private val _isCustomTheme = mutableStateOf(false)
    private val _isDynamicTheme = mutableStateOf(false)

    private val _themeVariantType = mutableStateOf(ThemeVariantType.EXPRESSIVE)

    private val _searchProvider = mutableStateOf(SearchProviders.OPEN_METEO)


    fun initPrefs(context: Context) {
        PreferencesHelper.init(context)

        _appTheme.value =
            PreferencesHelper.getString("app_theme") ?: "Dark"
        _customThemeColor.value = PreferencesHelper.getString("custom_theme_color") ?: "#2196f3"
        _isCustomTheme.value = PreferencesHelper.getBool("isCustomTheme") ?: false
        _isDynamicTheme.value = PreferencesHelper.getBool("isDynamicTheme") ?: false

        _themeVariantType.value = PreferencesHelper.getString("theme_variant_type")
            ?.let { runCatching { ThemeVariantType.valueOf(it) }.getOrNull() }
            ?: ThemeVariantType.EXPRESSIVE


        _searchProvider.value = PreferencesHelper.getString("searchProvider")
            ?.let { runCatching { SearchProviders.valueOf(it) }.getOrNull() }
            ?: SearchProviders.OPEN_METEO
    }

    @Composable
    fun state(): AppPrefsState = AppPrefsState(

        appTheme = _appTheme.value,
        setAppTheme = {
            _appTheme.value = it
            PreferencesHelper.setString("app_theme", it)
        },

        customThemeColor = _customThemeColor.value,
        setCustomThemeColor = {
            _customThemeColor.value = it
            PreferencesHelper.setString("custom_theme_color", it)
        },

        isCustomTheme = _isCustomTheme.value,
        setUseCustomTheme = {
            _isCustomTheme.value = it
            PreferencesHelper.setBool("isCustomTheme", it)
        },

        isDynamicTheme = _isDynamicTheme.value,
        setDynamicColor = {
            _isDynamicTheme.value = it
            PreferencesHelper.setBool("isDynamicTheme", it)
        },

        themeVariantType = _themeVariantType.value,
        setThemeVariantType = {
            _themeVariantType.value = it
            PreferencesHelper.setString("theme_variant_type", it.name)
        },

        searchProvider = _searchProvider.value,
        setSearchProvider = {
            _searchProvider.value = it
            PreferencesHelper.setString("searchProvider", it.name)
        }
    )
}