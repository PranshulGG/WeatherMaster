package com.pranshulgg.weather_master_app.feature.intro

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.domain.toMessageRes
import com.pranshulgg.weather_master_app.core.network.sources.address.nominatim.json.NominatimRepository
import com.pranshulgg.weather_master_app.core.prefs.AppPrefs
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weather_master_app.data.backup.BackupRepository
import com.pranshulgg.weather_master_app.data.backup.model.BackupPayload
import com.pranshulgg.weather_master_app.data.provider.devicelocation.DeviceLocation
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class IntroScreenViewModel @Inject constructor(
    val locationsRepo: LocationsRepository,
    @ApplicationContext private val context: Context,
    private val nominatimRepository: NominatimRepository,
    private val backupRepository: BackupRepository
) : ViewModel() {

    var isImportingBackup by mutableStateOf(false)
        private set

    fun saveDeviceLocation(location: DeviceLocation) {
        viewModelScope.launch {

            val address = try {
                nominatimRepository.getAddress(location.latitude, location.longitude)
            } catch (e: Exception) {
                null
            }

            if (address != null && address.city != null) {
                locationsRepo.saveLocation(
                    location.toDomain(context).copy(
                        name = address.city,
                        country = address.country,
                        countryCode = address.countryCode
                    )
                )
            } else {
                locationsRepo.saveLocation(location.toDomain(context))
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