package com.pranshulgg.weather_master_app.core.prefs


import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.pranshulgg.weather_master_app.core.model.sources.SearchSource
import com.pranshulgg.weather_master_app.core.ui.theme.ThemeVariantType
import com.pranshulgg.weather_master_app.core.prefs.helper.PreferencesHelper

object AppPrefs {
    private val _appTheme = mutableStateOf("Dark")
    private val _customThemeColor = mutableStateOf("#2196f3")
    private val _isCustomTheme = mutableStateOf(false)
    private val _isDynamicTheme = mutableStateOf(false)

    private val _themeVariantType = mutableStateOf(ThemeVariantType.EXPRESSIVE)

    private val _searchSource = mutableStateOf(SearchSource.OPEN_METEO)

    private val _backgroundUpdatesEnabled = mutableStateOf(false)

    private val _backgroundUpdatesInterval = mutableIntStateOf(60)

    private val _isFroggyLayout = mutableStateOf(true)
    private val _isShowWeatherAnimations = mutableStateOf(true)
    private val _isWeatherBasedTheme = mutableStateOf(true)

    private val _is24HrTimeFormat = mutableStateOf(true)

    private val _isShowSummary = mutableStateOf(true)


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


        _searchSource.value = PreferencesHelper.getString("searchSource")
            ?.let { runCatching { SearchSource.valueOf(it) }.getOrNull() }
            ?: SearchSource.OPEN_METEO

        _backgroundUpdatesEnabled.value =
            PreferencesHelper.getBool("backgroundUpdatesEnabled") ?: false


        _backgroundUpdatesInterval.intValue =
            PreferencesHelper.getInt("backgroundUpdatesInterval") ?: 60

        _isFroggyLayout.value = PreferencesHelper.getBool("isFroggyLayout") ?: true
        _isShowWeatherAnimations.value =
            PreferencesHelper.getBool("isShowWeatherAnimations") ?: true
        _isWeatherBasedTheme.value = PreferencesHelper.getBool("isWeatherBasedTheme") ?: true

        _is24HrTimeFormat.value = PreferencesHelper.getBool("is24HrTimeFormat") ?: true

        _isShowSummary.value = PreferencesHelper.getBool("isShowSummary") ?: true


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

        searchSource = _searchSource.value,
        setSearchSource = {
            _searchSource.value = it
            PreferencesHelper.setString("searchSource", it.name)
        },

        backgroundUpdatesEnabled = _backgroundUpdatesEnabled.value,
        setBackgroundUpdates = {
            _backgroundUpdatesEnabled.value = it
            PreferencesHelper.setBool("backgroundUpdatesEnabled", it)
        },

        backgroundUpdatesInterval = _backgroundUpdatesInterval.intValue,
        setBackgroundUpdatesInterval = {
            _backgroundUpdatesInterval.intValue = it
            PreferencesHelper.setInt("backgroundUpdatesInterval", it)
        },

        isFroggyLayout = _isFroggyLayout.value,
        setFroggyLayout = {
            _isFroggyLayout.value = it
            PreferencesHelper.setBool("isFroggyLayout", it)
        },


        isShowWeatherAnimations = _isShowWeatherAnimations.value,
        setShowWeatherAnimations = {
            _isShowWeatherAnimations.value = it
            PreferencesHelper.setBool("isShowWeatherAnimations", it)
        },

        isWeatherBasedTheme = _isWeatherBasedTheme.value,
        setIsWeatherBasedTheme = {
            _isWeatherBasedTheme.value = it
            PreferencesHelper.setBool("isWeatherBasedTheme", it)
        },

        is24HrTimeFormat = _is24HrTimeFormat.value,
        set24HrTimeFormat = {
            _is24HrTimeFormat.value = it
            PreferencesHelper.setBool("is24HrTimeFormat", it)
        },

        isShowSummary = _isShowSummary.value,
        setShowSummary = {
            _isShowSummary.value = it
            PreferencesHelper.setBool("isShowSummary", it)
        }
    )
}