package com.pranshulgg.weather_master_app.data.store

import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlock
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.List


data class WeatherBlocksStoreState(
    val blocks: List<WeatherBlock> = WeatherBlock.getDefault(),
    val dailyBlocks: List<WeatherBlock> = WeatherBlock.getDefaultForDaily(),
)

@Singleton
class WeatherBlocksStore @Inject constructor() {
    private val _data = MutableStateFlow(WeatherBlocksStoreState())
    val data = _data.asStateFlow()

    fun set(blocks: List<WeatherBlock>) {
        _data.update {
            it.copy(blocks = blocks)
        }
    }

    fun setForDaily(dailyBlocks: List<WeatherBlock>) {
        _data.update {
            it.copy(dailyBlocks = dailyBlocks)
        }
    }

}