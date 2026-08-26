package com.pranshulgg.weather_master_app.feature.shared

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.domain.toMessageRes
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlock
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.sources.isGlobal
import com.pranshulgg.weather_master_app.core.model.sources.isSourceSupportedFor
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityResult
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResult
import com.pranshulgg.weather_master_app.core.model.weather.openmeteo.OpenMeteoModel
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherUnitsRepository
import com.pranshulgg.weather_master_app.data.worker.WeatherBackgroundUpdateScheduler
import com.pranshulgg.weather_master_app.domain.usecase.DeleteLocationUseCase
import com.pranshulgg.weather_master_app.domain.usecase.GetWeatherUseCase
import com.pranshulgg.weather_master_app.domain.usecase.LoadWeatherBlocksUseCase
import com.pranshulgg.weather_master_app.domain.usecase.SaveWeatherBlocksUseCase
import com.pranshulgg.weather_master_app.domain.usecase.UpdateLocationSourceUseCase
import com.pranshulgg.weather_master_app.feature.main.MainScreenWeatherUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val locationsRepo: LocationsRepository,
    appWeatherUnitsRepo: WeatherUnitsRepository,
    private val getWeatherUseCase: GetWeatherUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase,
    private val updateLocationSourceUseCase: UpdateLocationSourceUseCase,
    private val loadWeatherBlocksUseCase: LoadWeatherBlocksUseCase,
    private val saveWeatherBlocksUseCase: SaveWeatherBlocksUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private var _uiState = mutableStateOf(MainScreenWeatherUiState())
    val uiState: State<MainScreenWeatherUiState> = _uiState

    companion object {
        private val AUTO_REFRESH_INTERVAL = 45.minutes
    }

    // Registered on the process-wide lifecycle (same pattern as AppVisibility) rather than a
    // Compose LocalLifecycleOwner tied to a screen: a screen-scoped observer gets torn down and
    // recreated by ordinary in-app navigation, and Android replays a synthetic ON_START to any
    // newly-added observer when the Activity is already started, firing a spurious refresh on
    // every navigation instead of only on a genuine app resume. init{} runs exactly once for
    // this ViewModel's lifetime, so this observer is only ever added once.
    private val processLifecycleObserver = object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            startAutoRefresh()
            val location = _uiState.value.activeLocation ?: return
            // isInitialized guard avoids duplicating setActiveLocation()'s cold-start fetch.
            if (_uiState.value.isInitialized) {
                getWeather(location = location)
            }
        }

        override fun onStop(owner: LifecycleOwner) {
            stopAutoRefresh()
        }
    }

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(processLifecycleObserver)

        // LOAD DEFAULT ON START
        viewModelScope.launch {
            if (_uiState.value.activeLocation == null && _uiState.value.weather == null && !_uiState.value.isInitialized) {

                val isLocationsEmpty = locationsRepo.isLocationsEmpty()
                if (isLocationsEmpty) {
                    // Locations Empty? can't happen, likely a first launch
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
        isManualRefresh: Boolean = false,
        isForceRefresh: Boolean = false,
        isForceRefreshForAirQuality: Boolean = false,
        isForceRefreshForAlerts: Boolean = false
    ) {
        setLoading(true)
        weatherJob?.cancel()
        val startTime = System.currentTimeMillis()
        _uiState.value = _uiState.value.copy(
            isError = false,
            isUnsupportedSource = false,
            isAirQualityLoading = true
        )


        weatherJob = viewModelScope.launch {

            getWeatherUseCase(
                location = location,
                isManualRefresh = isManualRefresh,
                isForceRefresh = isForceRefresh,
                isForceRefreshForAirQuality = isForceRefreshForAirQuality,
                isForceRefreshForAlerts = isForceRefreshForAlerts,
                onLocationUpdated = { updatedLocation ->
                    _uiState.value = _uiState.value.copy(activeLocation = updatedLocation)
                },
                onWeather = { result, effectiveLocation ->
                    handleWeatherData(result, effectiveLocation)
                },
                onAlerts = { result ->
                    handleAlerts(result)
                },
                onAirQuality = { result ->
                    handleAirQuality(result)
                },
            )

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
            deleteLocationUseCase(id)

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
        getWeather(location)
    }


    fun handleSourceChangeForWeather(
        location: Location,
        source: Source,
        airQualitySource: Source,
        alertSource: Source,
        openMeteoModel: OpenMeteoModel
    ) {
        viewModelScope.launch {

            val updatedLocation = updateLocationSourceUseCase(
                location = location,
                source = source,
                airQualitySource = airQualitySource,
                alertSource = alertSource,
                openMeteoModel = openMeteoModel
            )

            val allowForceRefreshForWeather =
                location.source != source || location.openMeteoModel != openMeteoModel
            val allowForceRefreshForAirQuality = location.airQualitySource != airQualitySource
            val allowForceRefreshForAlerts = location.alertSource != alertSource

            _uiState.value = _uiState.value.copy(activeLocation = updatedLocation)
            getWeather(
                updatedLocation,
                isForceRefresh = allowForceRefreshForWeather,
                isForceRefreshForAirQuality = allowForceRefreshForAirQuality,
                isForceRefreshForAlerts = allowForceRefreshForAlerts
            )
        }
    }


    fun saveBlocks(
        items: List<WeatherBlock>,
        isDaily: Boolean = false
    ) {

        viewModelScope.launch {
            saveWeatherBlocksUseCase(items, isDaily)

        }
        _uiState.value = _uiState.value.copy(blocks = items)

    }

    suspend fun loadBlocks() {
        val loadedBlocks = loadWeatherBlocksUseCase()
        _uiState.value = _uiState.value.copy(blocks = loadedBlocks)
    }

    private suspend fun handleWeatherData(result: WeatherResult, location: Location) {


        when (result) {

            is WeatherResult.Success -> {
                _uiState.value = _uiState.value.copy(weather = result.weather, isInitialized = true)
            }

            is WeatherResult.Error -> {

                val appExpectation = result.exception.toAppException()
                SnackbarManager.show(appExpectation.toMessageRes())

                _uiState.value = _uiState.value.copy(
                    isError = true,
                    weather = result.cacheWeather,
                    isUnsupportedSource = !location.source.isGlobal() && !location.source.isSourceSupportedFor(
                        location.countryCode?.uppercase()
                    )
                )
            }

            is WeatherResult.RefreshNotAvailable -> {
                SnackbarManager.show(R.string.weather_refresh_delay, messageArgs = 15)
            }

        }

        if (location.isDefault && !_uiState.value.isError && _uiState.value.weather != null) {
            WeatherBackgroundUpdateScheduler.updateAllWidgets(
                context,
                _uiState.value.weather!!,
                _uiState.value.weatherUnits
            )
        }
    }

    private fun handleAirQuality(
        result: AirQualityResult?
    ) {

        // No repository resolves for this location's airQualitySource (e.g. NONE) - clear
        // rather than leaving a previous location's air quality on screen.
        if (result == null) {
            _uiState.value = _uiState.value.copy(
                airQuality = null,
                isAirQualityLoading = false
            )
            return
        }

        when (result) {
            is AirQualityResult.Success -> {
                _uiState.value =
                    _uiState.value.copy(
                        airQuality = result.airquality,
                        isAirQualityLoading = false
                    )


            }

            // Fail silently, we just won't show the Air quality in the UI
            is AirQualityResult.Error -> {
                _uiState.value = _uiState.value.copy(
                    airQuality = result.cacheAirQuality,
                    isAirQualityLoading = false
                )
            }
        }
    }


    private fun handleAlerts(result: AlertResult?) {

        // No repository resolves for this location's alertSource (e.g. NONE) - clear rather
        // than leaving a previous location's alerts on screen.
        if (result == null) {
            _uiState.value = _uiState.value.copy(alerts = emptyList())
            return
        }

        when (result) {
            is AlertResult.Success -> {
                _uiState.value =
                    _uiState.value.copy(
                        alerts = result.alerts
                    )
            }

            is AlertResult.Error -> {
                _uiState.value = _uiState.value.copy(
                    alerts = result.cacheAlerts
                )
            }
        }
    }

    private var autoRefreshJob: Job? = null

    fun startAutoRefresh() {

        if (autoRefreshJob?.isActive == true) return

        autoRefreshJob = viewModelScope.launch {
            while (isActive) {

                delay(AUTO_REFRESH_INTERVAL)
                val location = _uiState.value.activeLocation ?: continue

                if (_uiState.value.isLoading || _uiState.value.isError) {
                    continue
                }

                getWeather(
                    location = location
                )
            }
        }
    }

    fun stopAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = null
    }

    override fun onCleared() {
        super.onCleared()
        ProcessLifecycleOwner.get().lifecycle.removeObserver(processLifecycleObserver)
    }
}