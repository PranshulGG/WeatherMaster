package com.pranshulgg.weather_master_app.domain.usecase

import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DeleteLocationUseCaseTest {

    private val repository: LocationsRepository = mockk(relaxed = true)
    private val useCase = DeleteLocationUseCase(repository)

    @Test
    fun `invoke should call deleteLocation on repository`() = runTest {
        // Given
        val locationId = "test_id"

        // When
        useCase(locationId)

        // Then
        coVerify { repository.deleteLocation(locationId) }
    }
}