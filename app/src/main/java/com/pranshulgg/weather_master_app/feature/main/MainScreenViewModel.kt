package com.pranshulgg.weather_master_app.feature.main

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.network.github.GithubRepository
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val githubRepository: GithubRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private var _uiState = mutableStateOf(MainScreenUiState())
    val uiState: State<MainScreenUiState> = _uiState

    init {
        viewModelScope.launch {
            checkForUpdates()
        }
    }

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

    private suspend fun checkForUpdates() {
        val isNewAvailable = try {
            githubRepository.isNewVersionAvailable(
                "v${
                    context.packageManager.getPackageInfo(
                        context.packageName,
                        0
                    ).versionName
                }"
            )
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            return
        }

        _uiState.value = _uiState.value.copy(isNewVersionAvailable = isNewAvailable)
    }

    fun dismissNewVersionSnackbar() {
        _uiState.value = _uiState.value.copy(isNewVersionAvailable = false)

    }
}