package com.pranshulgg.weather_master_app.feature.shared

import android.content.Context
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherUnitsRepository
import com.pranshulgg.weather_master_app.domain.usecase.*
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class WeatherViewModelTest {

    private val locationsRepo = mockk<LocationsRepository>(relaxed = true)
    private val appWeatherUnitsRepo = mockk<WeatherUnitsRepository>(relaxed = true)
    private val getWeatherUseCase = mockk<GetWeatherUseCase>(relaxed = true)
    private val updateLocationSourceUseCase = mockk<UpdateLocationSourceUseCase>(relaxed = true)
    private val deleteLocationUseCase = mockk<DeleteLocationUseCase>(relaxed = true)
    private val loadWeatherBlocksUseCase = mockk<LoadWeatherBlocksUseCase>(relaxed = true)
    private val saveWeatherBlocksUseCase = mockk<SaveWeatherBlocksUseCase>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)

    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setup() {
        viewModel = WeatherViewModel(
            locationsRepo,
            appWeatherUnitsRepo,
            getWeatherUseCase,
            updateLocationSourceUseCase,
            deleteLocationUseCase,
            loadWeatherBlocksUseCase,
            saveWeatherBlocksUseCase,
            context
        )
    }

    @Test
    fun `initial state should be default`() {
        assert(viewModel.uiState.value.locations.isEmpty())
    }
}