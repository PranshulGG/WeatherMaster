package com.pranshulgg.weathermaster.feature.search

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weathermaster.core.model.Location
import com.pranshulgg.weathermaster.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weathermaster.data.repository.LocationsRepository
import com.pranshulgg.weathermaster.data.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    val repo: SearchRepository,
    val locationsRepo: LocationsRepository
) : ViewModel() {

    var results by mutableStateOf<List<Location>>(emptyList())
        private set

    var loading by mutableStateOf(false)
        private set


    fun search(query: String) {
        if (query.isEmpty() || loading) return

        viewModelScope.launch {
            loading = true
            val data = try {
                repo.search(query)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                SnackbarManager.show("Something went wrong. Please try again")
                return@launch
            } finally {
                loading = false
            }

            results = data
        }
    }

    fun saveLocation(location: Location) {
        viewModelScope.launch {
            locationsRepo.saveLocation(location)
        }
    }

    private val _uiState = mutableStateOf(SearchUiState())
    val uiState: MutableState<SearchUiState> = _uiState

    fun updateQuery(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
    }


}