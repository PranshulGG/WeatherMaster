package com.pranshulgg.weather_master_app.feature.intro

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.network.sources.address.nominatim.json.NominatimRepository
import com.pranshulgg.weather_master_app.data.provider.devicelocation.DeviceLocation
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class IntroScreenViewModel @Inject constructor(
    val locationsRepo: LocationsRepository,
    @ApplicationContext private val context: Context,
    private val nominatimRepository: NominatimRepository
) : ViewModel() {

    fun saveDeviceLocation(location: DeviceLocation) {
        viewModelScope.launch {

            val address = nominatimRepository.getAddress(location.latitude, location.longitude)

            if (address != null && address.city != null) {
                locationsRepo.saveLocation(
                    location.toDomain(context).copy(
                        name = address.city,
                        country = address.country
                    )
                )
            } else {
                locationsRepo.saveLocation(location.toDomain(context))
            }

        }

    }

}