package com.pranshulgg.weathermaster.feature.shared

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weathermaster.core.model.WeatherBlockType
import com.pranshulgg.weathermaster.core.model.WeatherProviders
import com.pranshulgg.weathermaster.core.model.WeatherResult
import com.pranshulgg.weathermaster.core.model.domain.Location
import com.pranshulgg.weathermaster.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weathermaster.core.ui.state.ActiveLocationStore
import com.pranshulgg.weathermaster.data.provider.WeatherRepositoryProvider
import com.pranshulgg.weathermaster.data.repository.AppWeatherUnitsRepository
import com.pranshulgg.weathermaster.data.repository.LocationsRepository
import com.pranshulgg.weathermaster.feature.main.MainScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repo: WeatherRepositoryProvider,
    private val locationsRepo: LocationsRepository,
    private val activeLocationStore: ActiveLocationStore,
    private val appWeatherUnitsRepo: AppWeatherUnitsRepository
) : ViewModel() {

    private var _uiState = mutableStateOf(MainScreenUiState())
    val uiState: State<MainScreenUiState> = _uiState


    init {
        // LOAD DEFAULT ON START
        if (activeLocationStore.active.value == null) {
            locationsRepo.getDefaultLocation()
                .filterNotNull()
                .take(1)
                .onEach { location ->
                    if (activeLocationStore.active.value?.id != location.id) {
                        activeLocationStore.set(location)
                    }
                }
                .launchIn(viewModelScope)
        }

        // KEEP TRACK OF ACTIVE LOCATION
        activeLocationStore.active.filterNotNull().distinctUntilChanged().onEach {
            _uiState.value =
                _uiState.value.copy(activeLocation = it)
            getWeather(it, it.provider)
        }.launchIn(viewModelScope)


        // KEEP TRACK OF ALL LOCATIONS
        locationsRepo.getLocations()
            .onEach { locations ->
                _uiState.value = _uiState.value.copy(locations = locations)
            }
            .launchIn(viewModelScope)


        // KEEP TRACK OF APP UNITS
        appWeatherUnitsRepo.getUnits().distinctUntilChanged().onEach {
            _uiState.value = _uiState.value.copy(weatherUnits = it)
        }.launchIn(viewModelScope)
    }


    fun getWeather(location: Location, provider: WeatherProviders, isRefresh: Boolean = false) {
        setLoading(true)
        val startTime = System.currentTimeMillis()
        _uiState.value = _uiState.value.copy(isError = false)

        val currentRepo = repo.getRepository(provider)

        viewModelScope.launch {
            when (val result = currentRepo.getWeather(location, isRefresh)) {

                is WeatherResult.Success -> {
                    _uiState.value = _uiState.value.copy(weather = result.weather)
                }

                is WeatherResult.Error -> {

                    SnackbarManager.show("Error occurred. Please try again")

                    _uiState.value =
                        _uiState.value.copy(isError = true, weather = result.cacheWeather)
                }

                is WeatherResult.RefreshNotAvailable -> {
                    SnackbarManager.show("Please wait 15mins before refreshing")
                }
            }

            val elapsed = System.currentTimeMillis() - startTime
            val minLoadingTime = 1000L // 1s

            // Prevents loader flicker when responses return too quickly
            if (elapsed < minLoadingTime) {
                delay(minLoadingTime - elapsed)
            }

            setLoading(false)
        }

    }


    fun deleteLocation(id: String) {
        viewModelScope.launch {
            locationsRepo.deleteLocation(id)
        }
    }


    fun setLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }

    fun setActiveLocation(location: Location) {
        activeLocationStore.set(location)
    }


    // TODO: USE ROOM INSTEAD
    fun saveBlocksOrder(
        context: Context,
        items: List<WeatherBlockType>
    ) {
        val prefs = context.getSharedPreferences(
            "weather_blocks_prefs",
            Context.MODE_PRIVATE
        )
        prefs.edit {
            putString(
                "block_order",
                items.joinToString(",") { it.name }
            )
        }
    }

    fun loadBlocksOrder(context: Context): List<WeatherBlockType> {
        val prefs = context.getSharedPreferences(
            "weather_blocks_prefs",
            Context.MODE_PRIVATE
        )
        val saved = prefs.getString(
            "block_order",
            null
        )

        return saved?.split(",")?.mapNotNull {
            runCatching {
                WeatherBlockType.valueOf(it)
            }.getOrNull()
        }
            ?: listOf(
                WeatherBlockType.HUMIDITY_BLOCK,
                WeatherBlockType.VISIBILITY_BLOCK,
                WeatherBlockType.UV_INDEX_BLOCK,
                WeatherBlockType.PRESSURE_BLOCK,
                WeatherBlockType.SUN_BLOCK
            )
    }

}
