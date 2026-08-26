package com.pranshulgg.weather_master_app.domain.usecase

import com.pranshulgg.weather_master_app.data.repository.WeatherBlocksRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SaveWeatherBlocksUseCaseTest {

    private val weatherBlocksRepository = mockk<WeatherBlocksRepository>(relaxed = true)
    private lateinit var saveWeatherBlocksUseCase: SaveWeatherBlocksUseCase

    @Before
    fun setup() {
        saveWeatherBlocksUseCase = SaveWeatherBlocksUseCase(weatherBlocksRepository)
    }

    @Test
    fun `invoke should call repository saveBlocks`() = runTest {
        saveWeatherBlocksUseCase(emptyList(), isDaily = true)
        coVerify { weatherBlocksRepository.saveBlocks(any(), isDaily = true) }
    }
}