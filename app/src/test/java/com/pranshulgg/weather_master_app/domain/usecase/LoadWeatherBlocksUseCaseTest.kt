package com.pranshulgg.weather_master_app.domain.usecase

import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlock
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlockType
import com.pranshulgg.weather_master_app.data.repository.WeatherBlocksRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class LoadWeatherBlocksUseCaseTest {

    private val repository: WeatherBlocksRepository = mockk()
    private val useCase = LoadWeatherBlocksUseCase(repository)

    @Test
    fun `invoke should return blocks from repository`() = runTest {
        // Given
        val expectedBlocks = listOf(
            WeatherBlock(id = 1, isDaily = false, type = WeatherBlockType.HUMIDITY_BLOCK, isHidden = false, position = 0)
        )
        coEvery { repository.loadBlocks(false) } returns expectedBlocks

        // When
        val result = useCase(isDaily = false)

        // Then
        assertEquals(expectedBlocks, result)
    }

    @Test
    fun `invoke with isDaily true should return daily blocks from repository`() = runTest {
        // Given
        val expectedBlocks = listOf(
            WeatherBlock(id = 2, isDaily = true, type = WeatherBlockType.WIND_BLOCK, isHidden = false, position = 0)
        )
        coEvery { repository.loadBlocks(true) } returns expectedBlocks

        // When
        val result = useCase(isDaily = true)

        // Then
        assertEquals(expectedBlocks, result)
    }
}