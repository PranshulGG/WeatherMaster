package com.pranshulgg.weather_master_app.feature.settings.backup

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.domain.toMessageRes
import com.pranshulgg.weather_master_app.core.prefs.AppPrefs
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weather_master_app.data.backup.BackupRepository
import com.pranshulgg.weather_master_app.data.backup.model.BackupPayload
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class BackupScreenViewModel @Inject constructor(
    private val backupRepository: BackupRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var loading by mutableStateOf(false)
        private set

    var includeApiKeys by mutableStateOf(false)
        private set

    fun onIncludeApiKeysChanged(value: Boolean) {
        includeApiKeys = value
    }

    fun exportTo(uri: Uri) {
        viewModelScope.launch {
            loading = true
            try {
                val json = backupRepository.serializeBackup(includeApiKeys)
                context.contentResolver.openOutputStream(uri)?.use {
                    it.write(json.toByteArray(Charsets.UTF_8))
                } ?: throw AppException.BackupFileIOError()

                SnackbarManager.show(R.string.message_backup_exported)
            } catch (e: IOException) {
                SnackbarManager.show(AppException.BackupFileIOError().toMessageRes())
            } catch (e: Exception) {
                SnackbarManager.show(e.toAppException().toMessageRes())
            } finally {
                loading = false
            }
        }
    }

    fun importFrom(uri: Uri) {
        viewModelScope.launch {
            loading = true
            try {
                val text = context.contentResolver.openInputStream(uri)?.use {
                    it.readBytes().decodeToString()
                } ?: throw AppException.BackupFileIOError()

                val payload = Json { ignoreUnknownKeys = true }
                    .decodeFromString(BackupPayload.serializer(), text)

                backupRepository.restoreBackup(payload)
                AppPrefs.initPrefs(context)

                SnackbarManager.show(R.string.message_backup_restored)
            } catch (e: SerializationException) {
                SnackbarManager.show(AppException.BackupFileCorrupted().toMessageRes())
            } catch (e: IllegalArgumentException) {
                SnackbarManager.show(AppException.BackupFileCorrupted().toMessageRes())
            } catch (e: IOException) {
                SnackbarManager.show(AppException.BackupFileIOError().toMessageRes())
            } catch (e: Exception) {
                SnackbarManager.show(e.toAppException().toMessageRes())
            } finally {
                loading = false
            }
        }
    }
}
