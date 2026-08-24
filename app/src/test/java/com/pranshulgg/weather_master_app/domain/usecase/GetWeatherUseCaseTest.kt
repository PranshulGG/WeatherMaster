package com.pranshulgg.weather_master_app.domain.usecase

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import com.pranshulgg.weather_master_app.data.repository.data.SourceDataRepository
import io.mockk.coEvery
import io.mockk.coInvoke
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runTest
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
        coEvery {
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
        } returns mockk<Job>()

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
    fun `invoke with device location should update position if changed`() = runTest {
        // Given
        val deviceLocation = dummyLocation.copy(isDeviceLocation = true)
        val updatedLocation = deviceLocation.copy(latitude = 52.0)
        
        coEvery { locationsRepo.updateDeviceLocationPosition() } returns true
        coEvery { locationsRepo.getLocationForId(deviceLocation.id) } returns updatedLocation
        coEvery {
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
        } returns mockk<Job>()

        // When
        useCase(
            location = deviceLocation,
            onWeather = { _, _ -> },
            onAlerts = {},
            onAirQuality = {}
        )

        // Then
        coVerify { locationsRepo.updateDeviceLocationPosition() }
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
}