package com.pranshulgg.weather_master_app.domain.usecase

import android.content.Context
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import com.pranshulgg.weather_master_app.data.repository.data.SourceDataRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetWeatherUseCaseTest {

    private val locationsRepo = mockk<LocationsRepository>(relaxed = true)
    private val sourceDataRepository = mockk<SourceDataRepository>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)
    private lateinit var getWeatherUseCase: GetWeatherUseCase

    @Before
    fun setup() {
        getWeatherUseCase = GetWeatherUseCase(locationsRepo, sourceDataRepository, context)
    }

    @Test
    fun `invoke should call sourceDataRepository getData`() = runTest {
        val location = mockk<Location>(relaxed = true) {
            every { isDeviceLocation } returns false
        }
        val source = Source.OPEN_METEO
        val weatherUnits = WeatherUnits.getDefault()

        getWeatherUseCase(
            location = location,
            source = source,
            weatherUnits = weatherUnits,
            onWeather = { _, _ -> },
            onAlerts = {},
            onAirQuality = {}
        )

        coVerify {
            sourceDataRepository.getData(
                location = any(),
                isManualRefresh = any(),
                isForceRefresh = any(),
                isForceRefreshForAirQuality = any(),
                isForceRefreshForAlerts = any(),
                onWeather = any(),
                onAlerts = any(),
                onAirQuality = any()
            )
        }
    }
}