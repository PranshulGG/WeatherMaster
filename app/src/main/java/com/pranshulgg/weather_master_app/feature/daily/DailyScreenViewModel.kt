package com.pranshulgg.weather_master_app.feature.daily

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.core.managers.WeatherBlocksManager
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlock
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.data.repository.WeatherContextRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherBlocksRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherUnitsRepository
import com.pranshulgg.weather_master_app.data.store.InitializationStore
import com.pranshulgg.weather_master_app.data.store.LocationStore
import com.pranshulgg.weather_master_app.data.store.WeatherBlocksStore
import com.pranshulgg.weather_master_app.data.store.WeatherStore
import com.pranshulgg.weather_master_app.data.store.WeatherUnitsStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyScreenViewModel @Inject constructor(
    private val locationsRepo: WeatherContextRepository,
    private val weatherBlocksRepository: WeatherBlocksRepository,
    private val weatherUnitsRepository: WeatherUnitsRepository,
    weatherStore: WeatherStore,
    locationStore: LocationStore,
    weatherUnitsStore: WeatherUnitsStore,
    weatherBlocksStore: WeatherBlocksStore,
    private val weatherBlocksManager: WeatherBlocksManager

) : ViewModel() {

    val weather = weatherStore.data
    val location = locationStore.data
    val units = weatherUnitsStore.data
    val weatherBlocks = weatherBlocksStore.data

    fun saveBlocks(blocks: List<WeatherBlock>) {
        viewModelScope.launch {
            weatherBlocksManager.saveBlocks(items = blocks)
        }
    }
}