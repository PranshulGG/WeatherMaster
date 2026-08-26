package com.pranshulgg.weather_master_app.feature.daily

import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherUnitsRepository
import com.pranshulgg.weather_master_app.domain.usecase.LoadWeatherBlocksUseCase
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class DailyScreenViewModelTest {

    private val locationsRepo = mockk<LocationsRepository>(relaxed = true)
    private val loadWeatherBlocksUseCase = mockk<LoadWeatherBlocksUseCase>(relaxed = true)
    private val weatherUnitsRepository = mockk<WeatherUnitsRepository>(relaxed = true)

    private lateinit var viewModel: DailyScreenViewModel

    @Before
    fun setup() {
        viewModel = DailyScreenViewModel(
            locationsRepo,
            loadWeatherBlocksUseCase,
            weatherUnitsRepository
        )
    }

    @Test
    fun `initial state should be default`() {
        assert(viewModel.uiState.value.blocks.isEmpty())
    }
}