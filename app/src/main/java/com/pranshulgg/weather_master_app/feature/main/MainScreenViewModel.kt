package com.pranshulgg.weather_master_app.feature.main

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
) : ViewModel() {

    private var _uiState = mutableStateOf(MainScreenUiState())
    val uiState: State<MainScreenUiState> = _uiState


    fun showWeatherSourcesForLocationSheet(isLoading: Boolean) {
        if (isLoading) {
            SnackbarManager.show(R.string.error_refresh_waiting_before_action)
            return
        }
        _uiState.value = _uiState.value.copy(isWeatherSourcesForLocationSheetOpen = true)
    }

    fun hideWeatherSourcesForLocationSheet() {
        _uiState.value = _uiState.value.copy(isWeatherSourcesForLocationSheetOpen = false)
    }

    fun showWeatherSourcesInfoForLocationSheet() {
        _uiState.value = _uiState.value.copy(isWeatherSourcesInfoForLocationSheetOpen = true)
    }

    fun hideWeatherSourcesInfoForLocationSheet() {
        _uiState.value = _uiState.value.copy(isWeatherSourcesInfoForLocationSheetOpen = false)
    }
}