package com.pranshulgg.weather_master_app.core.managers

import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.data.repository.WeatherUnitsRepository
import com.pranshulgg.weather_master_app.data.store.WeatherUnitsStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class WeatherUnitsManager @Inject constructor(
    private val weatherUnitsStore: WeatherUnitsStore,
    private val weatherUnitsRepository: WeatherUnitsRepository
) {
    fun initialize(scope: CoroutineScope) {
        weatherUnitsRepository.getUnits().distinctUntilChanged().onEach {
            weatherUnitsStore.set(it)
        }.launchIn(scope)
    }

}
