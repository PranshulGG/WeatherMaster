package com.pranshulgg.weather_master_app.feature.search

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.sources.SearchSource
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.domain.toMessageRes
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.network.sources.search.geonames.GeoNamesTimezoneRepository
import com.pranshulgg.weather_master_app.core.prefs.AppPrefsState
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weather_master_app.data.provider.SearchRepositoryProvider
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    val repo: SearchRepositoryProvider,
    val locationsRepo: LocationsRepository,
    val geoNamesTimezoneRepository: GeoNamesTimezoneRepository
) : ViewModel() {

    var results by mutableStateOf<List<Location>>(emptyList())
        private set

    var loading by mutableStateOf(false)
        private set


    fun search(query: String) {
        if (query.isEmpty() || loading) return

        val currentRepo = repo.getRepository(_uiState.value.source)

        viewModelScope.launch {
            loading = true
            val data = try {
                currentRepo.search(query)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                SnackbarManager.show(R.string.error_generic)
                return@launch
            } finally {
                loading = false
            }

            results = data
        }
    }

    fun saveLocation(
        location: Location?,
        onBack: () -> Unit,
        onReset: () -> Unit,
        weatherSource: WeatherSource
    ) {
        if (location == null) return

        viewModelScope.launch {
            try {
                val resolved = if (_uiState.value.source == SearchSource.GEO_NAMES) {
                    resolveGeoNamesLocation(location)
                } else {
                    location
                }
                locationsRepo.saveLocation(resolved.copy(source = weatherSource))
                onBack()
            } catch (e: Exception) {
                val appExpectation = e.toAppException()
                SnackbarManager.show(appExpectation.toMessageRes())
                onReset()
            }
        }
    }

    // For some reason geo names do not provide timezone in searchJson, which is annoying asf
    private suspend fun resolveGeoNamesLocation(location: Location): Location {
        val data = geoNamesTimezoneRepository.getTimeZone(location.latitude, location.longitude)
        check(!data?.timezone.isNullOrEmpty()) { "Timezone data missing for location: $location" }
        return location.copy(timezone = data.timezone)
    }

    private val _uiState = mutableStateOf(SearchUiState())
    val uiState: MutableState<SearchUiState> = _uiState

    fun updateQuery(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
    }

    fun updateSource(source: SearchSource, prefs: AppPrefsState) {
        _uiState.value = _uiState.value.copy(source = source)
        prefs.setSearchSource(source)
    }

    fun showSearchSourcePickerSheet() {
        _uiState.value = _uiState.value.copy(isSearchSourcePickerSheetOpen = true)
    }


    fun hideSearchSourcePickerSheet() {
        _uiState.value = _uiState.value.copy(isSearchSourcePickerSheetOpen = false)
    }

    fun removeResults() {
        results = emptyList()
    }

    fun showWeatherSourcesForLocationSheet() {
        _uiState.value = _uiState.value.copy(isWeatherSourcesForLocationSheetOpen = true)
    }


    fun hideWeatherSourcesForLocationSheet() {
        _uiState.value = _uiState.value.copy(isWeatherSourcesForLocationSheetOpen = false)
    }
}