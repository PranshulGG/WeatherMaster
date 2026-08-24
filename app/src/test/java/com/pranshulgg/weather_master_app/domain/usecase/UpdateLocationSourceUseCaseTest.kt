package com.pranshulgg.weather_master_app.domain.usecase

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.openmeteo.OpenMeteoModel
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherDataReconcilerRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class UpdateLocationSourceUseCaseTest {

    private val locationsRepo: LocationsRepository = mockk(relaxed = true)
    private val weatherDataReconcilerRepository: WeatherDataReconcilerRepository = mockk(relaxed = true)
    private val useCase = UpdateLocationSourceUseCase(locationsRepo, weatherDataReconcilerRepository)

    private val dummyLocation = Location(
        id = "1",
        name = "London",
        latitude = 51.5,
        longitude = -0.12,
        country = "UK",
        timezone = "GMT",
        countryCode = "GB",
        state = "",
        isDefault = true,
        source = Source.OPEN_METEO
    )

    @Test
    fun `invoke should update location sources and reconcile data`() = runTest {
        // Given
        val newSource = Source.MET_NORWAY
        val newAirQualitySource = Source.ACCU_WEATHER
        val newAlertSource = Source.NWS
        val newModel = OpenMeteoModel.BEST_MATCH

        // When
        val result = useCase(
            location = dummyLocation,
            source = newSource,
            airQualitySource = newAirQualitySource,
            alertSource = newAlertSource,
            openMeteoModel = newModel
        )

        // Then
        coVerify { locationsRepo.updateSourceForLocation(dummyLocation.id, newSource) }
        coVerify { locationsRepo.updateAirQualitySourceForLocation(dummyLocation.id, newAirQualitySource) }
        coVerify { locationsRepo.updateAlertSourceForLocation(dummyLocation.id, newAlertSource) }
        coVerify { locationsRepo.updateOpenMeteoModelForLocation(dummyLocation.id, newModel) }
        
        coVerify { 
            weatherDataReconcilerRepository.reconcileSourceChange(
                previous = dummyLocation,
                updated = any()
            ) 
        }
        
        assertEquals(newSource, result.source)
        assertEquals(newAirQualitySource, result.airQualitySource)
        assertEquals(newAlertSource, result.alertSource)
        assertEquals(newModel, result.openMeteoModel)
    }
}