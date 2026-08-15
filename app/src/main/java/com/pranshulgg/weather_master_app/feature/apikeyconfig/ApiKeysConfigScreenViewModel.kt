package com.pranshulgg.weather_master_app.feature.apikeyconfig

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.ApiKey
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.data.repository.ApiKeysRepository
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ApiKeysConfigScreenViewModel @Inject constructor(
    private val apiKeysRepo: ApiKeysRepository
) : ViewModel() {

    var apiKeys by mutableStateOf<List<ApiKey>>(emptyList())
        private set

    init {
        getApiKeys()
    }

    private fun getApiKeys() {
        viewModelScope.launch {
            apiKeys = apiKeysRepo.getAllApiKeys()
        }
    }

    fun saveKey(key: String, source: WeatherSource) {
        viewModelScope.launch {
            apiKeysRepo.updateApiKeyForSource(source, key)
            getApiKeys()
        }
    }
}
