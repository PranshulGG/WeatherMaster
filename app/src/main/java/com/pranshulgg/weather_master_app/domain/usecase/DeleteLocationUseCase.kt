package com.pranshulgg.weather_master_app.domain.usecase

import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import javax.inject.Inject

/**
 * Use case to delete a location and its associated data from the local database.
 */
class DeleteLocationUseCase @Inject constructor(
    private val locationsRepo: LocationsRepository
) {
    suspend operator fun invoke(id: String) {
        locationsRepo.deleteLocation(id)
    }
}