/** Initial Clean Architecture Domain Layer integration implemented by https://github.com/gietabhi10 */
package com.pranshulgg.weather_master_app.domain.usecase

import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlock
import com.pranshulgg.weather_master_app.data.repository.WeatherBlocksRepository
import javax.inject.Inject

class SaveWeatherBlocksUseCase @Inject constructor(
    private val weatherBlocksRepository: WeatherBlocksRepository
) {
    suspend operator fun invoke(items: List<WeatherBlock>, isDaily: Boolean = false) {
        weatherBlocksRepository.saveBlocks(items.map {
            WeatherBlock(
                type = it.type,
                isHidden = it.isHidden,
                position = it.position,
                isDaily = isDaily,
                id = it.id
            )
        }, isDaily)
    }
}