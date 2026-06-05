package com.pranshulgg.weather_master_app.feature.shared

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.domain.toMessageRes
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlock
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityResult
import com.pranshulgg.weather_master_app.core.network.github.GithubRepository
import com.pranshulgg.weather_master_app.core.network.sources.address.nominatim.json.NominatimRepository
import com.pranshulgg.weather_master_app.core.network.sources.airquality.openmeteo.OpenMeteoAqiRepository
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weather_master_app.data.provider.WeatherRepositoryProvider
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherBlocksRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherDataReconcilerRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherUnitsRepository
import com.pranshulgg.weather_master_app.data.worker.WeatherUpdateScheduler
import com.pranshulgg.weather_master_app.feature.main.MainScreenWeatherUiState
import dagger.hilt.android.internal.Contexts
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repo: WeatherRepositoryProvider,
    private val locationsRepo: LocationsRepository,
    appWeatherUnitsRepo: WeatherUnitsRepository,
    private val weatherBlocksRepository: WeatherBlocksRepository,
    private val openMeteoAqiRepository: OpenMeteoAqiRepository,
    private val weatherDataReconcilerRepository: WeatherDataReconcilerRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private var _uiState = mutableStateOf(MainScreenWeatherUiState())
    val uiState: State<MainScreenWeatherUiState> = _uiState


    init {

        // LOAD DEFAULT ON START
        viewModelScope.launch {
            if (_uiState.value.activeLocation == null && _uiState.value.weather == null && !_uiState.value.isInitialized) {

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
        locationsRepo.getLocations().distinctUntilChanged()
            .onEach { locations ->

                val previous = _uiState.value.locations

                if (previous.isNotEmpty()) {

                    val newLocation = locations.firstOrNull { new ->
                        previous.none { it.id == new.id }
                    }

                    newLocation?.let {
                        if (!_uiState.value.isLoading) {
                            setActiveLocation(it)
                        }
                    }
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
        source: WeatherSource,
        isManualRefresh: Boolean = false,
        isForceRefresh: Boolean = false
    ) {
        setLoading(true)
        weatherJob?.cancel()
        val startTime = System.currentTimeMillis()
        _uiState.value = _uiState.value.copy(isError = false)


        weatherJob = viewModelScope.launch {

            if (location.isDeviceLocation && isManualRefresh) {
                handleDeviceLocation()
            }

            // Run separately
            if (!_uiState.value.isError) {
                launch {
                    handleAirQuality(location, isManualRefresh)
                }
            }

            handleWeatherData(source, location, isManualRefresh, isForceRefresh)

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
        getWeather(location, location.source)
    }

    fun updateSourceForLocation(location: Location, source: WeatherSource) {
        val updatedLocation = location.copy(source = source)

        viewModelScope.launch {

            locationsRepo.updateSourceForLocation(location.id, source)
            val allowForceRefresh = location.source != source


            if (allowForceRefresh) {
                weatherDataReconcilerRepository.cleanUpStaleData(location.source, location.id)
            }
            _uiState.value = _uiState.value.copy(
                activeLocation = updatedLocation
            )
            getWeather(
                updatedLocation,
                source,
                isForceRefresh = allowForceRefresh
            )


        }
    }


    fun saveBlocks(
        items: List<WeatherBlock>,
        isDaily: Boolean = false
    ) {
        _uiState.value = _uiState.value.copy(blocks = items)

        viewModelScope.launch {
            weatherBlocksRepository.saveBlocks(items.map {
                WeatherBlock(
                    type = it.type,
                    isHidden = false,
                    position = it.position,
                    isDaily = isDaily,
                    id = it.id
                )
            }, isDaily)

        }

    }

    suspend fun loadBlocks() {
        val loadedBlocks = weatherBlocksRepository.loadBlocks()
        _uiState.value = _uiState.value.copy(blocks = loadedBlocks)
    }


    private suspend fun handleDeviceLocation() {
        locationsRepo.updateDeviceLocationPosition()
    }

    private suspend fun handleWeatherData(
        source: WeatherSource,
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean
    ) {

        val repo = repo.getRepository(source)


        when (val result = repo.getWeather(location, isManualRefresh, isForceRefresh)) {


            is WeatherResult.Success -> {
                _uiState.value = _uiState.value.copy(weather = result.weather, isInitialized = true)
            }

            is WeatherResult.Error -> {

                val appExpectation = result.exception.toAppException()
                SnackbarManager.show(appExpectation.toMessageRes())


                _uiState.value = _uiState.value.copy(
                    isError = true,
                    weather = result.cacheWeather
                )
            }

            is WeatherResult.RefreshNotAvailable -> {
                SnackbarManager.show(R.string.weather_refresh_delay, messageArgs = 15)
            }

        }

        if (location.isDefault && !_uiState.value.isError && _uiState.value.weather != null) {
            WeatherUpdateScheduler.updateAllWidgets(
                context,
                skipForegroundCheck = true,
                _uiState.value.weather!!,
                _uiState.value.weatherUnits
            )
        }
    }

    private suspend fun handleAirQuality(location: Location, isManualRefresh: Boolean) {

        when (val result = openMeteoAqiRepository.getAirQuality(location, isManualRefresh)) {
            is AirQualityResult.Success -> {
                _uiState.value = _uiState.value.copy(airQuality = result.airquality)
            }

            // Fail silently, we just won't show the Air quality in the UI
            is AirQualityResult.Error -> {
                _uiState.value = _uiState.value.copy(airQuality = result.cacheAirQuality)
            }
        }
    }


}
