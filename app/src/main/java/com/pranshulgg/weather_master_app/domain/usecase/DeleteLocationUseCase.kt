package com.pranshulgg.weather_master_app.domain.usecase

import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import javax.inject.Inject

class DeleteLocationUseCase @Inject constructor(
    private val locationsRepo: LocationsRepository
) {
    suspend operator fun invoke(id: String) {
        locationsRepo.deleteLocation(id)
    }
}