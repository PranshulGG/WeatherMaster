package com.pranshulgg.weather_master_app.feature.blocks

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.core.managers.WeatherBlocksManager
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.weather.rain.RainNowcast
import com.pranshulgg.weather_master_app.core.network.sources.weather.rain.RainNowcastRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherContextRepository
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
class BlocksScreenViewModel @Inject constructor(
    weatherStore: WeatherStore,
    locationStore: LocationStore,
    weatherUnitsStore: WeatherUnitsStore,
    private val rainNowcastRepository: RainNowcastRepository
) : ViewModel() {

    val weather = weatherStore.data
    val location = locationStore.data
    val units = weatherUnitsStore.data

    private var _rainNowcast = mutableStateOf<RainNowcast?>(null)
    val rainNowcast: State<RainNowcast?> = _rainNowcast

    /**
     * 2 hour rain nowcast for the block's location (KNMI radar, DWD radar or
     * the keyless Open-Meteo minutely-15 fallback). Null when the location has
     * no nowcast coverage.
     */
    fun getRainNowcast(location: Location) {
        viewModelScope.launch {
            val nowcast = try {
                rainNowcastRepository.getNowcast(location)
            } catch (e: Exception) {
                null
            }
            _rainNowcast.value = nowcast
        }
    }

}
