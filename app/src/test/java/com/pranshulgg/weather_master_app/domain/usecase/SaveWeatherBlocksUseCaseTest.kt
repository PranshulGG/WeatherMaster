package com.pranshulgg.weather_master_app.domain.usecase

import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlock
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlockType
import com.pranshulgg.weather_master_app.data.repository.WeatherBlocksRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SaveWeatherBlocksUseCaseTest {

    private val repository: WeatherBlocksRepository = mockk(relaxed = true)
    private val useCase = SaveWeatherBlocksUseCase(repository)

    @Test
    fun `invoke should call saveBlocks on repository`() = runTest {
        // Given
        val blocks = listOf(
            WeatherBlock(id = 1, isDaily = false, type = WeatherBlockType.HUMIDITY_BLOCK, isHidden = false, position = 0)
        )

        // When
        useCase(blocks, isDaily = false)

        // Then
        coVerify { repository.saveBlocks(blocks, false) }
    }

    @Test
    fun `invoke with isDaily true should call saveBlocks with isDaily true`() = runTest {
        // Given
        val blocks = listOf(
            WeatherBlock(id = 2, isDaily = true, type = WeatherBlockType.WIND_BLOCK, isHidden = false, position = 0)
        )

        // When
        useCase(blocks, isDaily = true)

        // Then
        coVerify { repository.saveBlocks(blocks, true) }
    }
}