package com.pranshulgg.weather_master_app.feature.intro

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.domain.toMessageRes
import com.pranshulgg.weather_master_app.core.model.domain.weather.ApiKey
import com.pranshulgg.weather_master_app.core.model.sources.Capability
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.sources.getSourcesForCountry
import com.pranshulgg.weather_master_app.core.network.sources.address.nominatim.json.NominatimRepository
import com.pranshulgg.weather_master_app.core.prefs.AppPrefs
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weather_master_app.data.backup.BackupRepository
import com.pranshulgg.weather_master_app.data.backup.model.BackupPayload
import com.pranshulgg.weather_master_app.data.provider.devicelocation.DeviceLocation
import com.pranshulgg.weather_master_app.data.repository.ApiKeysRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherContextRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class IntroScreenViewModel @Inject constructor(
    val locationsRepo: WeatherContextRepository,
    @ApplicationContext private val context: Context,
    private val nominatimRepository: NominatimRepository,
    private val backupRepository: BackupRepository,
    private val apiKeysRepo: ApiKeysRepository
) : ViewModel() {

    var isImportingBackup by mutableStateOf(false)
        private set

    var pendingDeviceLocation by mutableStateOf<Location?>(null)
        private set

    var isWeatherSourcesForLocationSheetOpen by mutableStateOf(false)
        private set

    var apiKeys by mutableStateOf<List<ApiKey>>(emptyList())
        private set

    // Resolves the device's location and shows the weather-source picker instead of saving
    // straight away with a hardcoded source - see #1184.
    fun saveDeviceLocation(location: DeviceLocation) {
        viewModelScope.launch {

            val address = try {
                nominatimRepository.getAddress(location.latitude, location.longitude)
            } catch (e: Exception) {
                null
            }

            val resolved = if (address != null && address.city != null) {
                location.toDomain(context).copy(
                    name = address.city,
                    country = address.country,
                    countryCode = address.countryCode
                )
            } else {
                location.toDomain(context)
            }

            pendingDeviceLocation = resolved
            apiKeys = apiKeysRepo.getAllApiKeys()
            isWeatherSourcesForLocationSheetOpen = true
        }

    }

    fun hideWeatherSourcesForLocationSheet() {
        isWeatherSourcesForLocationSheetOpen = false
    }

    fun confirmDeviceLocationSource(weatherSource: Source) {
        val resolved = pendingDeviceLocation ?: return

        viewModelScope.launch {
            try {
                val alertSource = if (Capability.ALERTS in weatherSource.capabilities) {
                    weatherSource
                } else {
                    getSourcesForCountry(resolved.countryCode?.uppercase())
                        .firstOrNull { Capability.ALERTS in it.capabilities }
                        ?: resolved.alertSource
                }

                locationsRepo.saveLocation(
                    resolved.copy(
                        source = weatherSource,
                        alertSource = alertSource
                    )
                )
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                SnackbarManager.show(e.toAppException().toMessageRes())
            } finally {
                isWeatherSourcesForLocationSheetOpen = false
                pendingDeviceLocation = null
            }
        }
    }

    fun importBackup(uri: Uri) {
        viewModelScope.launch {
            isImportingBackup = true
            try {
                val text = context.contentResolver.openInputStream(uri)?.use {
                    it.readBytes().decodeToString()
                } ?: throw AppException.BackupFileIOError()

                val payload = Json { ignoreUnknownKeys = true }
                    .decodeFromString(BackupPayload.serializer(), text)

                backupRepository.restoreBackup(payload)
                AppPrefs.initPrefs(context)
                // MainScreen watches the location list reactively and steps past this screen
                // itself once it's non-empty - no explicit navigation needed here.
            } catch (e: SerializationException) {
                SnackbarManager.show(AppException.BackupFileCorrupted().toMessageRes())
            } catch (e: IllegalArgumentException) {
                SnackbarManager.show(AppException.BackupFileCorrupted().toMessageRes())
            } catch (e: IOException) {
                SnackbarManager.show(AppException.BackupFileIOError().toMessageRes())
            } catch (e: Exception) {
                SnackbarManager.show(e.toAppException().toMessageRes())
            } finally {
                isImportingBackup = false
            }
        }
    }

}