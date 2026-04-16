package com.pranshulgg.weathermaster.feature.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weathermaster.core.model.Weather
import com.pranshulgg.weathermaster.core.model.WeatherProviders
import com.pranshulgg.weathermaster.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weathermaster.data.provider.WeatherRepositoryProvider
import com.pranshulgg.weathermaster.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    val repo: WeatherRepository
) : ViewModel() {

    var weather by mutableStateOf<Weather?>(null)
        private set


    fun getWeather() {
        viewModelScope.launch {
            val data = try {
                repo.getWeather(52.52, 13.419998)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                SnackbarManager.show("ERROR- $e")
                return@launch
            }

            weather = data
        }
    }
}