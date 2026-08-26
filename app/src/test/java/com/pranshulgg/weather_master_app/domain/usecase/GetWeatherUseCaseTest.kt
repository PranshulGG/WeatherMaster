package com.pranshulgg.weather_master_app.domain.usecase

import android.content.Context
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import com.pranshulgg.weather_master_app.data.repository.data.SourceDataRepository
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetWeatherUseCaseTest {

    private val locationsRepo = mockk<LocationsRepository>(relaxed = true)
    private val sourceDataRepository = mockk<SourceDataRepository>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)
    private lateinit var getWeatherUseCase: GetWeatherUseCase

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
        getWeatherUseCase = GetWeatherUseCase(locationsRepo, sourceDataRepository, context)
    }

    @Test
    fun `invoke should call sourceDataRepository getData`() = runTest {
        val weatherUnits = WeatherUnits.getDefault()

        getWeatherUseCase(
            location = dummyLocation,
            weatherUnits = weatherUnits,
            onWeather = { _, _ -> },
            onAlerts = {},
            onAirQuality = {}
        )

        coVerify {
            sourceDataRepository.getData(
                location = dummyLocation,
                isManualRefresh = false,
                isForceRefresh = false,
                isForceRefreshForAirQuality = false,
                isForceRefreshForAlerts = false,
                onWeather = any(),
                onAlerts = any(),
                onAirQuality = any()
            )
        }
    }

    @Test
    fun `invoke with device location should update position if changed`() = runTest {
        val deviceLocation = dummyLocation.copy(isDeviceLocation = true)
        val updatedLocation = deviceLocation.copy(latitude = 52.0)
        
        coEvery { locationsRepo.updateDeviceLocationPosition() } returns true
        coEvery { locationsRepo.getLocationForId(deviceLocation.id) } returns updatedLocation

        getWeatherUseCase(
            location = deviceLocation,
            weatherUnits = WeatherUnits.getDefault(),
            onWeather = { _, _ -> },
            onAlerts = {},
            onAirQuality = {}
        )

        coVerify { locationsRepo.updateDeviceLocationPosition() }
        coVerify {
            sourceDataRepository.getData(
                location = updatedLocation,
                isManualRefresh = any(),
                isForceRefresh = true,
                isForceRefreshForAirQuality = true,
                isForceRefreshForAlerts = true,
                onWeather = any(),
                onAlerts = any(),
                onAirQuality = any()
            )
        }
    }

    @Test
    fun `invoke should notify updated location via callback`() = runTest {
        val deviceLocation = dummyLocation.copy(isDeviceLocation = true)
        val updatedLocation = deviceLocation.copy(latitude = 52.0)
        var locationPassedToCallback: Location? = null
        
        coEvery { locationsRepo.updateDeviceLocationPosition() } returns true
        coEvery { locationsRepo.getLocationForId(deviceLocation.id) } returns updatedLocation

        getWeatherUseCase(
            location = deviceLocation,
            weatherUnits = WeatherUnits.getDefault(),
            onLocationUpdated = { locationPassedToCallback = it },
            onWeather = { _, _ -> },
            onAlerts = {},
            onAirQuality = {}
        )

        assertEquals(updatedLocation, locationPassedToCallback)
    }
}