package com.pranshulgg.weathermaster.feature.locations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weathermaster.core.model.domain.Location
import com.pranshulgg.weathermaster.core.ui.state.ActiveLocationStore
import com.pranshulgg.weathermaster.data.repository.LocationsRepository
import com.pranshulgg.weathermaster.data.repository.WeatherDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationsScreenViewModel @Inject constructor(
    private val weatherDataRepo: WeatherDataRepository
) : ViewModel() {

    val allLocationsWeather = weatherDataRepo.getAllLocationsWeather().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )


}