package com.pranshulgg.weather_master_app.feature.intro

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.data.provider.devicelocation.DeviceLocation
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class IntroScreenViewModel @Inject constructor(
    val locationsRepo: LocationsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    fun saveDeviceLocation(location: DeviceLocation) {
        viewModelScope.launch {
            locationsRepo.saveLocation(location.toDomain(context))
        }
    }

}