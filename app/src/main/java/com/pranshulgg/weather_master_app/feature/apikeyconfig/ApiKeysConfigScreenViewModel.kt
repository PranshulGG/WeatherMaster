package com.pranshulgg.weather_master_app.feature.apikeyconfig

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.core.model.domain.weather.ApiKey
import com.pranshulgg.weather_master_app.core.model.domain.weather.isNearExpiry
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.network.sources.weather.aemet.AemetRepository
import com.pranshulgg.weather_master_app.data.repository.ApiKeysRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ApiKeysConfigScreenViewModel @Inject constructor(
    private val apiKeysRepo: ApiKeysRepository,
    private val aemetRepository: AemetRepository
) : ViewModel() {

    var apiKeys by mutableStateOf<List<ApiKey>>(emptyList())
        private set

    // Only meaningful for AEMET right now, whose key is worth validating live since it can
    // expire; null means no save attempt has been made yet in this screen visit.
    var aemetKeyValidationResult by mutableStateOf<Boolean?>(null)
        private set

    // Confirmed via a live check, not just elapsed time - see checkAemetKeyExpiry.
    var isAemetKeyExpired by mutableStateOf(false)
        private set

    init {
        getApiKeys()
        checkAemetKeyExpiry()
    }

    private fun getApiKeys() {
        viewModelScope.launch {
            apiKeys = apiKeysRepo.getAllApiKeys()
        }
    }

    private fun checkAemetKeyExpiry() {
        viewModelScope.launch {
            val aemetKey = apiKeysRepo.getAllApiKeys().find { it.source == WeatherSource.AEMET }

            // Time elapsed is just a cheap gate to avoid hitting the network on every visit -
            // once we're past it, confirm with a real live check rather than trusting the guess.
            isAemetKeyExpired = if (aemetKey?.isNearExpiry() == true) {
                !aemetRepository.validateApiKey(aemetKey.apiKey.orEmpty())
            } else {
                false
            }
        }
    }

    fun saveKey(key: String, source: WeatherSource) {
        if (source == WeatherSource.AEMET) {
            saveAemetKey(key)
            return
        }

        viewModelScope.launch {
            apiKeysRepo.updateApiKeyForSource(source, key)
            getApiKeys()
        }
    }

    private fun saveAemetKey(key: String) {
        aemetKeyValidationResult = null
        viewModelScope.launch {
            val isValid = aemetRepository.validateApiKey(key)

            if (isValid) {
                apiKeysRepo.updateApiKeyForSource(WeatherSource.AEMET, key)
                getApiKeys()
                // savedAt just reset, so this key can't read as expired anymore.
                isAemetKeyExpired = false
            }

            aemetKeyValidationResult = isValid
        }
    }
}
