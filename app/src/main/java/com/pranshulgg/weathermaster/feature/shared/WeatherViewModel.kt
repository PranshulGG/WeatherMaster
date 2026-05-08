package com.pranshulgg.weathermaster.feature.shared

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.model.WeatherProviders
import com.pranshulgg.weathermaster.core.model.WeatherResult
import com.pranshulgg.weathermaster.core.model.domain.Location
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.model.domain.WeatherBlock
import com.pranshulgg.weathermaster.core.model.toAppException
import com.pranshulgg.weathermaster.core.model.toMessageRes
import com.pranshulgg.weathermaster.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weathermaster.data.provider.WeatherRepositoryProvider
import com.pranshulgg.weathermaster.data.repository.AppWeatherUnitsRepository
import com.pranshulgg.weathermaster.data.repository.LocationsRepository
import com.pranshulgg.weathermaster.data.repository.WeatherDataRepository
import com.pranshulgg.weathermaster.feature.main.MainScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repo: WeatherRepositoryProvider,
    private val locationsRepo: LocationsRepository,
    appWeatherUnitsRepo: AppWeatherUnitsRepository,
    private val weatherDataRepository: WeatherDataRepository
) : ViewModel() {

    private var _uiState = mutableStateOf(MainScreenUiState())
    val uiState: State<MainScreenUiState> = _uiState


    init {

        // LOAD DEFAULT ON START
        viewModelScope.launch {
            if (_uiState.value.activeLocation == null) {

                val isLocationsEmpty = locationsRepo.isLocationsEmpty()
                if (isLocationsEmpty) {
                    // Locations Empty? not possible, likely a first launch
                    _uiState.value = uiState.value.copy(isInitialized = true)
                }
                val default = locationsRepo.getDefaultLocation().filterNotNull().first()
                setActiveLocation(default)
            }
            loadBlocks()
        }

        // KEEP TRACK OF ALL LOCATIONS
        locationsRepo.getLocations()
            .onEach { locations ->

                val previous = _uiState.value.locations

                if (previous.isNotEmpty()) {

                    val newLocation = locations.firstOrNull { new ->
                        previous.none { it.id == new.id }
                    }

                    newLocation?.let { setActiveLocation(it) }
                }

                _uiState.value = _uiState.value.copy(locations = locations)
            }
            .launchIn(viewModelScope)


        // KEEP TRACK OF APP UNITS
        appWeatherUnitsRepo.getUnits().distinctUntilChanged().onEach {
            _uiState.value = _uiState.value.copy(weatherUnits = it)
        }.launchIn(viewModelScope)

    }

    private var weatherJob: Job? = null

    fun getWeather(
        location: Location,
        provider: WeatherProviders,
        isManualRefresh: Boolean = false
    ) {

        weatherJob?.cancel()
        setLoading(true)
        val startTime = System.currentTimeMillis()
        _uiState.value = _uiState.value.copy(isError = false)


        if (location.isDeviceLocation && isManualRefresh) {
            // UPDATE POSITION (adds loading time)
            // TODO: THIS IS TEMPORARY, HAS TO CHANGE LATER
            handleDeviceLocation()
        }

        val currentRepo = repo.getRepository(provider)

        weatherJob = viewModelScope.launch {
            when (val result = currentRepo.getWeather(location, isManualRefresh)) {

                is WeatherResult.Success -> {
                    _uiState.value =
                        _uiState.value.copy(weather = result.weather, isInitialized = true)
                }

                is WeatherResult.Error -> {

                    val appExpectation = result.exception.toAppException()
                    SnackbarManager.show(appExpectation.toMessageRes())

                    _uiState.value =
                        _uiState.value.copy(isError = true, weather = result.cacheWeather)
                }

                is WeatherResult.RefreshNotAvailable -> {
                    SnackbarManager.show(R.string.weather_refresh_delay, messageArgs = 15)
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

            if (_uiState.value.activeLocation?.id == id) {
                setActiveLocation(_uiState.value.locations.first { it.isDefault })
            }
        }
    }


    fun setLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }

    fun setActiveLocation(location: Location) {
        _uiState.value = _uiState.value.copy(activeLocation = location)
        getWeather(location, location.provider)
    }


    fun saveBlocks(
        items: List<WeatherBlock>
    ) {
        _uiState.value = _uiState.value.copy(blocks = items)

        viewModelScope.launch {
            weatherDataRepository.saveBlocks(items.map {
                WeatherBlock(
                    type = it.type,
                    isHidden = false,
                    position = it.position
                )
            })

        }

    }

    fun loadBlocks() {
        viewModelScope.launch {
            val loadedBlocks = weatherDataRepository.loadBlocks()
            _uiState.value = _uiState.value.copy(blocks = loadedBlocks)
        }
    }


    private fun handleDeviceLocation() {
        viewModelScope.launch {
            locationsRepo.updateDeviceLocationPosition()
        }
    }
}
