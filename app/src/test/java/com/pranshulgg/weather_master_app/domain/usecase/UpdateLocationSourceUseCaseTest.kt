package com.pranshulgg.weather_master_app.domain.usecase

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.openmeteo.OpenMeteoModel
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherDataReconcilerRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UpdateLocationSourceUseCaseTest {

    private val locationsRepo = mockk<LocationsRepository>(relaxed = true)
    private val weatherDataReconcilerRepository = mockk<WeatherDataReconcilerRepository>(relaxed = true)
    private lateinit var updateLocationSourceUseCase: UpdateLocationSourceUseCase

    @Before
    fun setup() {
        updateLocationSourceUseCase = UpdateLocationSourceUseCase(locationsRepo, weatherDataReconcilerRepository)
    }

    @Test
    fun `invoke should update repository and reconcile`() = runTest {
        val location = mockk<Location>(relaxed = true) {
            every { id } returns "1"
        }
        val source = Source.OPEN_METEO

        updateLocationSourceUseCase(
            location = location,
            source = source,
            airQualitySource = source,
            alertSource = source,
            openMeteoModel = OpenMeteoModel.BEST_MATCH
        )

        coVerify {
            locationsRepo.updateSourceForLocation("1", source)
            weatherDataReconcilerRepository.reconcileSourceChange(any(), any())
        }
    }
}