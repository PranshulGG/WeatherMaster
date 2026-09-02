package com.pranshulgg.weather_master_app.core.managers

import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlock
import com.pranshulgg.weather_master_app.data.repository.WeatherBlocksRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherUnitsRepository
import com.pranshulgg.weather_master_app.data.store.WeatherBlocksStore
import com.pranshulgg.weather_master_app.data.store.WeatherUnitsStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class WeatherBlocksManager @Inject constructor(
    private val weatherBlocksRepository: WeatherBlocksRepository,
    private val weatherBlocksStore: WeatherBlocksStore
) {
    suspend fun initialize() {
        val blocks = weatherBlocksRepository.loadBlocks()
        val dailyBlocks = weatherBlocksRepository.loadBlocks(isDaily = true)

        weatherBlocksStore.set(blocks)
        weatherBlocksStore.setForDaily(dailyBlocks)
    }

    suspend fun saveBlocks(
        items: List<WeatherBlock>,
        isDaily: Boolean = false
    ) {
        weatherBlocksRepository.saveBlocks(items.map {
            WeatherBlock(
                type = it.type,
                isHidden = false,
                position = it.position,
                isDaily = isDaily,
                id = it.id
            )
        }, isDaily)

        if (isDaily) {
            weatherBlocksStore.setForDaily(items)
        } else {
            weatherBlocksStore.set(items)
        }
    }
}
