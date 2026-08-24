package com.pranshulgg.weather_master_app.feature.shared

import android.content.Context
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherUnitsRepository
import com.pranshulgg.weather_master_app.domain.usecase.*
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private val locationsRepo: LocationsRepository = mockk()
    private val appWeatherUnitsRepo: WeatherUnitsRepository = mockk()
    private val getWeatherUseCase: GetWeatherUseCase = mockk()
    private val deleteLocationUseCase: DeleteLocationUseCase = mockk()
    private val updateLocationSourceUseCase: UpdateLocationSourceUseCase = mockk()
    private val loadWeatherBlocksUseCase: LoadWeatherBlocksUseCase = mockk()
    private val saveWeatherBlocksUseCase: SaveWeatherBlocksUseCase = mockk()
    private val context: Context = mockk()

    private lateinit var viewModel: WeatherViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val dummyLocation = Location(
        id = "1",
        name = "London",
        latitude = 51.5,
        longitude = -0.12,
        country = "UK",
        timezone = "GMT",
        countryCode = "GB",
        state = "",
        isDefault = true
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        every { locationsRepo.getLocations() } returns flowOf(emptyList())
        every { appWeatherUnitsRepo.getUnits() } returns flowOf(WeatherUnits.getDefault())
        every { locationsRepo.getDefaultLocation() } returns flowOf(dummyLocation)
        coEvery { locationsRepo.isLocationsEmpty() } returns false
        coEvery { loadWeatherBlocksUseCase() } returns emptyList()
        coJustRun {
            getWeatherUseCase(
                location = any(),
                isManualRefresh = any(),
                isForceRefresh = any(),
                isForceRefreshForAirQuality = any(),
                isForceRefreshForAlerts = any(),
                onLocationUpdated = any(),
                onWeather = any(),
                onAlerts = any(),
                onAirQuality = any()
            )
        }

        viewModel = WeatherViewModel(
            locationsRepo,
            appWeatherUnitsRepo,
            getWeatherUseCase,
            deleteLocationUseCase,
            updateLocationSourceUseCase,
            loadWeatherBlocksUseCase,
            saveWeatherBlocksUseCase,
            context
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setActiveLocation should call getWeatherUseCase`() = runTest {
        // When
        viewModel.setActiveLocation(dummyLocation)
        advanceUntilIdle()

        // Then
        coVerify {
            getWeatherUseCase(
                location = dummyLocation,
                isManualRefresh = false,
                isForceRefresh = false,
                isForceRefreshForAirQuality = false,
                isForceRefreshForAlerts = false,
                onLocationUpdated = any(),
                onWeather = any(),
                onAlerts = any(),
                onAirQuality = any()
            )
        }
    }

    @Test
    fun `deleteLocation should call deleteLocationUseCase`() = runTest {
        // Given
        coJustRun { deleteLocationUseCase(any()) }

        // When
        viewModel.deleteLocation("1")
        advanceUntilIdle()

        // Then
        coVerify { deleteLocationUseCase("1") }
    }

    @Test
    fun `device location update should update activeLocation in state`() = runTest {
        // Given
        val deviceLocation = dummyLocation.copy(isDeviceLocation = true)
        val updatedLocation = deviceLocation.copy(latitude = 52.0)
        val onLocationUpdatedSlot = slot<(Location) -> Unit>()
        
        coEvery {
            getWeatherUseCase(
                location = any(),
                isManualRefresh = any(),
                isForceRefresh = any(),
                isForceRefreshForAirQuality = any(),
                isForceRefreshForAlerts = any(),
                onLocationUpdated = capture(onLocationUpdatedSlot),
                onWeather = any(),
                onAlerts = any(),
                onAirQuality = any()
            )
        } coAnswers {
            onLocationUpdatedSlot.captured(updatedLocation)
        }

        // When
        viewModel.setActiveLocation(deviceLocation)
        advanceUntilIdle()

        // Then
        assertEquals(updatedLocation, viewModel.uiState.value.activeLocation)
    }

    @Test
    fun `handleSourceChangeForWeather should compute force refresh flags correctly`() = runTest {
        // Given
        val newSource = com.pranshulgg.weather_master_app.core.model.sources.Source.MET_NORWAY
        val updatedLocation = dummyLocation.copy(source = newSource)
        
        coEvery { 
            updateLocationSourceUseCase(
                location = any(),
                source = any(),
                airQualitySource = any(),
                alertSource = any(),
                openMeteoModel = any()
            ) 
        } returns updatedLocation

        // When
        viewModel.handleSourceChangeForWeather(
            location = dummyLocation,
            source = newSource,
            airQualitySource = dummyLocation.airQualitySource,
            alertSource = dummyLocation.alertSource,
            openMeteoModel = dummyLocation.openMeteoModel
        )
        advanceUntilIdle()

        // Then
        coVerify {
            getWeatherUseCase(
                location = updatedLocation,
                isForceRefresh = true, // Source changed
                isForceRefreshForAirQuality = false,
                isForceRefreshForAlerts = false,
                onLocationUpdated = any(),
                onWeather = any(),
                onAlerts = any(),
                onAirQuality = any()
            )
        }
    }
}