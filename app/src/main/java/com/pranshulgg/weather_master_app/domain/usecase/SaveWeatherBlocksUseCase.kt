package com.pranshulgg.weather_master_app.domain.usecase

/**
 * Initial Clean Architecture Domain Layer integration implemented by https://github.com/gietabhi10
 */

import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlock
import com.pranshulgg.weather_master_app.data.repository.WeatherBlocksRepository
import javax.inject.Inject

/**
 * Use case to save the order and visibility configuration of weather blocks to the database.
 */
class SaveWeatherBlocksUseCase @Inject constructor(
    private val weatherBlocksRepository: WeatherBlocksRepository
) {
    suspend operator fun invoke(items: List<WeatherBlock>, isDaily: Boolean = false) {
        weatherBlocksRepository.saveBlocks(items, isDaily)
    }
}