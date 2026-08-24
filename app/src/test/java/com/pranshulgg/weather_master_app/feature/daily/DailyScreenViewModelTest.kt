package com.pranshulgg.weather_master_app.feature.daily

import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlock
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlockType
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherUnitsRepository
import com.pranshulgg.weather_master_app.domain.usecase.LoadWeatherBlocksUseCase
import com.pranshulgg.weather_master_app.domain.usecase.SaveWeatherBlocksUseCase
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@kotlinx.coroutines.ExperimentalCoroutinesApi
class DailyScreenViewModelTest {

    private val locationsRepo: LocationsRepository = mockk()
    private val weatherUnitsRepository: WeatherUnitsRepository = mockk()
    private val loadWeatherBlocksUseCase: LoadWeatherBlocksUseCase = mockk()
    private val saveWeatherBlocksUseCase: SaveWeatherBlocksUseCase = mockk()

    private lateinit var viewModel: DailyScreenViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = DailyScreenViewModel(
            locationsRepo,
            weatherUnitsRepository,
            loadWeatherBlocksUseCase,
            saveWeatherBlocksUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveBlocks should call saveWeatherBlocksUseCase with isDaily true`() = runTest {
        // Given
        val blocks = listOf(
            WeatherBlock(id = 1, isDaily = true, type = WeatherBlockType.HUMIDITY_BLOCK, isHidden = false, position = 0)
        )
        coJustRun { saveWeatherBlocksUseCase(blocks, true) }

        // When
        viewModel.saveBlocks(blocks)

        // Then
        coVerify { saveWeatherBlocksUseCase(blocks, true) }
    }
}