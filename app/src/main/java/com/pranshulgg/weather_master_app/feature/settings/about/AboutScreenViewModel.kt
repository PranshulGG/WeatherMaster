package com.pranshulgg.weather_master_app.feature.settings.about

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.domain.toMessageRes
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.network.github.GithubRepository


@HiltViewModel
class AboutScreenViewModel @Inject constructor(
    private val githubRepository: GithubRepository
) : ViewModel() {

    var loading by mutableStateOf(false)
        private set


    suspend fun isNewVersionAvailable(currentTag: String, onAction: () -> Unit) {
        loading = true
        val result = try {

            githubRepository.isNewVersionAvailable(
                currentTag = currentTag
            )

        } catch (e: Exception) {
            val appExpectation = e.toAppException()
            SnackbarManager.show(appExpectation.toMessageRes())
            return
        } finally {
            loading = false
        }

        if (result) {
            SnackbarManager.show(R.string.message_new_version_available, onAction = {
                onAction()
            }, actionLabel = R.string.action_view)
        } else {
            SnackbarManager.show(R.string.message_using_latest_version)
        }
    }


}