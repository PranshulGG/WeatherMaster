package com.pranshulgg.weather_master_app.feature.main

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.managers.WeatherBlocksManager
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlock
import com.pranshulgg.weather_master_app.core.network.github.GithubRepository
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weather_master_app.data.store.InitializationStore
import com.pranshulgg.weather_master_app.data.store.LocationStore
import com.pranshulgg.weather_master_app.data.store.WeatherBlocksStore
import com.pranshulgg.weather_master_app.data.store.WeatherStore
import com.pranshulgg.weather_master_app.data.store.WeatherUnitsStore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val githubRepository: GithubRepository,
    @ApplicationContext private val context: Context,
    weatherStore: WeatherStore,
    locationStore: LocationStore,
    initializationStore: InitializationStore,
    weatherUnitsStore: WeatherUnitsStore,
    weatherBlocksStore: WeatherBlocksStore,
    private val weatherBlocksManager: WeatherBlocksManager
) : ViewModel() {

    val weather = weatherStore.data
    val location = locationStore.data
    val initialization = initializationStore.data
    val units = weatherUnitsStore.data
    val weatherBlocks = weatherBlocksStore.data

    private var _uiState = mutableStateOf(MainScreenUiState())
    val uiState: State<MainScreenUiState> = _uiState

    init {
        viewModelScope.launch {
            if (!_uiState.value.isGooglePlayStoreRelease) {
                checkForUpdates()
            }
        }
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

    fun hideChangelogSheet() {
        _uiState.value = _uiState.value.copy(isChangelogSheetOpen = false)
    }

    fun showChangelogSheet() {
        _uiState.value = _uiState.value.copy(isChangelogSheetOpen = true)
    }

    fun saveBlocks(blocks: List<WeatherBlock>) {
        viewModelScope.launch {
            weatherBlocksManager.saveBlocks(items = blocks)
        }
    }
}