package com.pranshulgg.weathermaster.core.prefs


import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import com.pranshulgg.weathermaster.core.utils.PreferencesHelper

object AppPrefs {
    private val _appTheme = mutableStateOf("System")
    private val _customThemeColor = mutableStateOf("#2196f3")
    private val _isCustomTheme = mutableStateOf(false)
    private val _isDynamicTheme = mutableStateOf(false)

    private val _isFirstStart = mutableStateOf(true)


    fun initPrefs(context: Context) {
        PreferencesHelper.init(context)

        _appTheme.value =
            PreferencesHelper.getString("app_theme") ?: "System"
        _customThemeColor.value = PreferencesHelper.getString("custom_theme_color") ?: "#2196f3"
        _isCustomTheme.value = PreferencesHelper.getBool("isCustomTheme") ?: false
        _isDynamicTheme.value = PreferencesHelper.getBool("isDynamicTheme") ?: false
        _isFirstStart.value = PreferencesHelper.getBool("isFirstStart") ?: true
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
        setCustomTheme = {
            _isCustomTheme.value = it
            PreferencesHelper.setBool("isCustomTheme", it)
        },

        isDynamicTheme = _isDynamicTheme.value,
        setDynamicColor = {
            _isDynamicTheme.value = it
            PreferencesHelper.setBool("isDynamicTheme", it)
        },

        isFirstStart = _isFirstStart.value,
        setFirstStart = {
            _isFirstStart.value = it
            PreferencesHelper.setBool("isFirstStart", it)
        }

    )
}