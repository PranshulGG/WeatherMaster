package com.pranshulgg.weather_master_app.feature.main

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.weather.isNearExpiry
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.network.github.GithubRepository
import com.pranshulgg.weather_master_app.core.network.sources.weather.aemet.AemetRepository
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weather_master_app.data.repository.ApiKeysRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val githubRepository: GithubRepository,
    private val apiKeysRepository: ApiKeysRepository,
    private val aemetRepository: AemetRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private var _uiState = mutableStateOf(MainScreenUiState())
    val uiState: State<MainScreenUiState> = _uiState

    init {
        viewModelScope.launch {
            if (!_uiState.value.isGooglePlayStoreRelease) {
                checkForUpdates()
            }
        }
        viewModelScope.launch {
            checkAemetKeyExpiry()
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

    fun hideUnsupportedSelectedSourceDialog() {
        _uiState.value = _uiState.value.copy(isUnsupportedSourceDialogOpen = false)
    }

    fun showUnsupportedSelectedSourceDialog() {
        _uiState.value = _uiState.value.copy(isUnsupportedSourceDialogOpen = true)
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

    private suspend fun checkAemetKeyExpiry() {
        val aemetKey = apiKeysRepository.getAllApiKeys().find { it.source == WeatherSource.AEMET }

        // Time elapsed is just a cheap gate to avoid hitting the network on every app open -
        // once we're past it, confirm with a real live check rather than trusting the guess.
        val isExpired = if (aemetKey?.isNearExpiry() == true) {
            !aemetRepository.validateApiKey(aemetKey.apiKey.orEmpty())
        } else {
            false
        }

        _uiState.value = _uiState.value.copy(isAemetKeyExpiring = isExpired)
    }

    // Re-run whenever MainScreen is revisited (e.g. returning from the API key screen),
    // so the banner reflects reality instead of a one-time dismiss.
    fun refreshAemetKeyExpiryStatus() {
        viewModelScope.launch {
            checkAemetKeyExpiry()
        }
    }

    fun hideChangelogSheet() {
        _uiState.value = _uiState.value.copy(isChangelogSheetOpen = false)
    }

    fun showChangelogSheet() {
        _uiState.value = _uiState.value.copy(isChangelogSheetOpen = true)
    }
}