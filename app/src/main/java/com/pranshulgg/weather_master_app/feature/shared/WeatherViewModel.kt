package com.pranshulgg.weather_master_app.feature.shared

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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
import com.pranshulgg.weather_master_app.data.repository.WeatherBlocksRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherDataReconcilerRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherUnitsRepository
import com.pranshulgg.weather_master_app.data.repository.data.SourceDataRepository
import com.pranshulgg.weather_master_app.data.worker.WeatherUpdateScheduler
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
    private val weatherBlocksRepository: WeatherBlocksRepository,
    private val weatherDataReconcilerRepository: WeatherDataReconcilerRepository,
    private val sourceDataRepository: SourceDataRepository,
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
        source: Source,
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

            var effectiveLocation = location
            var effectiveForceRefresh = isForceRefresh
            var effectiveForceRefreshForAirQuality = isForceRefreshForAirQuality
            var effectiveForceRefreshForAlerts = isForceRefreshForAlerts

            // Checked regardless of isManualRefresh so it also runs on app-open/auto-refresh,
            // not just pull-to-refresh. If the device actually moved, force a real fetch for
            // the new coordinates instead of trusting a cache keyed to the old ones (note:
            // isForceRefresh bypasses the cache unconditionally, unlike isManualRefresh, which
            // only relaxes the cache TTL and would still be blocked by the 15-min throttle).
            if (location.isDeviceLocation) {
                val positionChanged = handleDeviceLocation()
                if (positionChanged) {
                    effectiveLocation = locationsRepo.getLocationForId(location.id)
                    effectiveForceRefresh = true
                    effectiveForceRefreshForAirQuality = true
                    effectiveForceRefreshForAlerts = true
                    _uiState.value = _uiState.value.copy(activeLocation = effectiveLocation)
                }
            }


            sourceDataRepository.getData(
                location = effectiveLocation,
                isManualRefresh = isManualRefresh,
                isForceRefresh = effectiveForceRefresh,
                isForceRefreshForAirQuality = effectiveForceRefreshForAirQuality,
                isForceRefreshForAlerts = effectiveForceRefreshForAlerts,
                onWeather = { result ->
                    handleWeatherData(result, effectiveLocation)
                },
                onAlerts = { result ->
                    handleAlerts(result)
                },
                onAirQuality = { result ->
                    handleAirQuality(result)
                },
            )
//            handleWeatherData(source, effectiveLocation, isManualRefresh, effectiveForceRefresh)

            // Run separately
//            if (!_uiState.value.isError) {
//                launch {
//                    handleAirQuality(
//                        effectiveLocation,
//                        isManualRefresh,
//                        effectiveForceRefreshForAirQuality
//                    )
//                }
//

            // Only run separately if the alert source is a different API of its own
            // If not, then run after the weather has fetched
//                if (!source.providesAlerts && effectiveLocation.alertSource.name != source.name) {
//                    launch {
//                        handleAlerts(
//                            effectiveLocation,
//                            isManualRefresh,
//                            effectiveForceRefreshForAlerts
//                        )
//                    }
//                } else {
//                    handleAlertsFromWeatherSource(effectiveLocation)
//                }
//            }


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


    fun handleSourceChangeForWeather(
        location: Location,
        source: Source,
        airQualitySource: Source,
        alertSource: Source,
        openMeteoModel: OpenMeteoModel
    ) {
        val updatedLocation = location.copy(
            source = source,
            airQualitySource = airQualitySource,
            alertSource = alertSource,
            openMeteoModel = openMeteoModel
        )

        viewModelScope.launch {

            locationsRepo.updateSourceForLocation(location.id, source)
            locationsRepo.updateAirQualitySourceForLocation(location.id, airQualitySource)
            locationsRepo.updateAlertSourceForLocation(location.id, alertSource)
            locationsRepo.updateOpenMeteoModelForLocation(location.id, openMeteoModel)

            val allowForceRefreshForWeather =
                location.source != source || location.openMeteoModel != openMeteoModel
            val allowForceRefreshForAirQuality = location.airQualitySource != airQualitySource
            val allowForceRefreshForAlerts = location.alertSource != alertSource

            weatherDataReconcilerRepository.reconcileSourceChange(
                previous = location,
                updated = updatedLocation
            )
            _uiState.value = _uiState.value.copy(activeLocation = updatedLocation)
            getWeather(
                updatedLocation,
                source,
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
        _uiState.value = _uiState.value.copy(blocks = items)

    }

    suspend fun loadBlocks() {
        val loadedBlocks = weatherBlocksRepository.loadBlocks()
        _uiState.value = _uiState.value.copy(blocks = loadedBlocks)
    }


    private suspend fun handleDeviceLocation(): Boolean {
        return locationsRepo.updateDeviceLocationPosition()
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
            WeatherUpdateScheduler.updateAllWidgets(
                context,
                _uiState.value.weather!!,
                _uiState.value.weatherUnits
            )
        }
    }

    private fun handleAirQuality(
        result: AirQualityResult
    ) {

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


    private fun handleAlerts(result: AlertResult) {

        when (result) {
            is AlertResult.Success -> {
                _uiState.value =
                    _uiState.value.copy(
                        alerts = result.alerts
                    )
            }

            // Fail silently, we just won't show the Air quality in the UI
            is AlertResult.Error -> {
                _uiState.value = _uiState.value.copy(
                    alerts = result.cacheAlerts
                )
            }
        }
    }

    private var autoRefreshJob: Job? = null

    fun startAutoRefresh(
        location: Location,
        source: Source
    ) {

        if (autoRefreshJob?.isActive == true) return

        autoRefreshJob = viewModelScope.launch {
            while (isActive) {

                delay(45.minutes)
                if (_uiState.value.isLoading || _uiState.value.isError) {
                    continue
                }

                getWeather(
                    location = location,
                    source = source
                )
            }
        }
    }

    fun stopAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = null
    }
}
