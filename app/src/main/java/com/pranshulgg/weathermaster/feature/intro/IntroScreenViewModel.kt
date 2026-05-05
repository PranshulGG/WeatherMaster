package com.pranshulgg.weathermaster.feature.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weathermaster.core.model.domain.Location
import com.pranshulgg.weathermaster.data.repository.LocationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class IntroScreenViewModel @Inject constructor(
    val locationsRepo: LocationsRepository
) : ViewModel() {

    fun saveDeviceLocation(location: Location) {
        viewModelScope.launch {
            locationsRepo.saveLocation(location)
        }
    }

}