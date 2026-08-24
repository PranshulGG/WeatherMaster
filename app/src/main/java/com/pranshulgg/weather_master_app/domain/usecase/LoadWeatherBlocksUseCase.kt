package com.pranshulgg.weather_master_app.domain.usecase

/**
 * Initial Clean Architecture Domain Layer integration implemented by https://github.com/gietabhi10
 */

import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlock
import com.pranshulgg.weather_master_app.data.repository.WeatherBlocksRepository
import javax.inject.Inject

/**
 * Use case to load the configured weather blocks (e.g. Humidity, UV Index) for a screen.
 *
 * It handles fetching the saved order/visibility from the database and falling back
 * to defaults if no custom configuration exists.
 */
class LoadWeatherBlocksUseCase @Inject constructor(
    private val weatherBlocksRepository: WeatherBlocksRepository
) {
    suspend operator fun invoke(isDaily: Boolean = false): List<WeatherBlock> {
        return weatherBlocksRepository.loadBlocks(isDaily)
    }
}