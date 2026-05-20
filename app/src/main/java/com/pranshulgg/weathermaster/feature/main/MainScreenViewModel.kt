package com.pranshulgg.weathermaster.feature.main

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
) : ViewModel() {

    private var _uiState = mutableStateOf(MainScreenUiState())
    val uiState: State<MainScreenUiState> = _uiState


    fun showWeatherSourceDialog(isLoading: Boolean) {
        if (isLoading) return
        _uiState.value = _uiState.value.copy(isWeatherSourcesDialogOpen = true)
    }

    fun hideWeatherSourceDialog() {
        _uiState.value = _uiState.value.copy(isWeatherSourcesDialogOpen = false)
    }

}