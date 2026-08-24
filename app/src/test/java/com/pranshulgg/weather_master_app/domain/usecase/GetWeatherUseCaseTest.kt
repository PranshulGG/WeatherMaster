package com.pranshulgg.weather_master_app.domain.usecase

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import com.pranshulgg.weather_master_app.data.repository.data.SourceDataRepository
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetWeatherUseCaseTest {

    private val locationsRepo: LocationsRepository = mockk()
    private val sourceDataRepository: SourceDataRepository = mockk()
    private val useCase = GetWeatherUseCase(locationsRepo, sourceDataRepository)

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

    @Test
    fun `invoke should call sourceDataRepository getData`() = runTest {
        // Given
        coJustRun {
            sourceDataRepository.getData(
                location = dummyLocation,
                isManualRefresh = any(),
                isForceRefresh = any(),
                isForceRefreshForAirQuality = any(),
                isForceRefreshForAlerts = any(),
                onWeather = any(),
                onAlerts = any(),
                onAirQuality = any()
            )
        }

        // When
        useCase(
            location = dummyLocation,
            onWeather = { _, _ -> },
            onAlerts = {},
            onAirQuality = {}
        )

        // Then
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
    fun `invoke with device location should update position and notify if changed`() = runTest {
        // Given
        val deviceLocation = dummyLocation.copy(isDeviceLocation = true)
        val updatedLocation = deviceLocation.copy(latitude = 52.0)
        var locationPassedToCallback: Location? = null
        
        coEvery { locationsRepo.updateDeviceLocationPosition() } returns true
        coEvery { locationsRepo.getLocationForId(deviceLocation.id) } returns updatedLocation
        coJustRun {
            sourceDataRepository.getData(
                location = updatedLocation,
                isManualRefresh = any(),
                isForceRefresh = any(),
                isForceRefreshForAirQuality = any(),
                isForceRefreshForAlerts = any(),
                onWeather = any(),
                onAlerts = any(),
                onAirQuality = any()
            )
        }

        // When
        useCase(
            location = deviceLocation,
            onLocationUpdated = { locationPassedToCallback = it },
            onWeather = { _, _ -> },
            onAlerts = {},
            onAirQuality = {}
        )

        // Then
        coVerify { locationsRepo.updateDeviceLocationPosition() }
        assertEquals(updatedLocation, locationPassedToCallback)
        coVerify {
            sourceDataRepository.getData(
                location = updatedLocation,
                isManualRefresh = false,
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
    fun `invoke should bubble up onWeather result`() = runTest {
        // Given
        val errorResult = WeatherResult.Error(Exception("Network error"), null)
        var resultPassedToCallback: WeatherResult? = null

        coEvery {
            sourceDataRepository.getData(
                location = any(),
                isManualRefresh = any(),
                isForceRefresh = any(),
                isForceRefreshForAirQuality = any(),
                isForceRefreshForAlerts = any(),
                onWeather = any(),
                onAlerts = any(),
                onAirQuality = any()
            )
        } coAnswers {
            val onWeatherCallback = it.invocation.args[5] as suspend (WeatherResult) -> Unit
            onWeatherCallback(errorResult)
        }

        // When
        useCase(
            location = dummyLocation,
            onWeather = { result, _ -> resultPassedToCallback = result },
            onAlerts = {},
            onAirQuality = {}
        )

        // Then
        assertEquals(errorResult, resultPassedToCallback)
    }
}