package com.pranshulgg.weather_master_app.domain.usecase

import com.pranshulgg.weather_master_app.data.repository.WeatherBlocksRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class LoadWeatherBlocksUseCaseTest {

    private val weatherBlocksRepository = mockk<WeatherBlocksRepository>(relaxed = true)
    private lateinit var loadWeatherBlocksUseCase: LoadWeatherBlocksUseCase

    @Before
    fun setup() {
        loadWeatherBlocksUseCase = LoadWeatherBlocksUseCase(weatherBlocksRepository)
    }

    @Test
    fun `invoke should call repository loadBlocks`() = runTest {
        loadWeatherBlocksUseCase(isDaily = true)
        coVerify { weatherBlocksRepository.loadBlocks(isDaily = true) }
    }
}