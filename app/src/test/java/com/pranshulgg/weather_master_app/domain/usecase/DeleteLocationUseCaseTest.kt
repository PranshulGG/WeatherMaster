package com.pranshulgg.weather_master_app.domain.usecase

import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DeleteLocationUseCaseTest {

    private val locationsRepo = mockk<LocationsRepository>(relaxed = true)
    private lateinit var deleteLocationUseCase: DeleteLocationUseCase

    @Before
    fun setup() {
        deleteLocationUseCase = DeleteLocationUseCase(locationsRepo)
    }

    @Test
    fun `invoke should call repository deleteLocation`() = runTest {
        val id = "1"
        deleteLocationUseCase(id)
        coVerify { locationsRepo.deleteLocation(id) }
    }
}