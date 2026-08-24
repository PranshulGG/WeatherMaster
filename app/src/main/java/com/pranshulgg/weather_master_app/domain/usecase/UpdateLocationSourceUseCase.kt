package com.pranshulgg.weather_master_app.domain.usecase

/**
 * Initial Clean Architecture Domain Layer integration implemented by https://github.com/gietabhi10
 */

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.weather.openmeteo.OpenMeteoModel
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherDataReconcilerRepository
import javax.inject.Inject

/**
 * Use case to update weather, air quality, and alert sources for a specific location.
 *
 * This use case updates the location configuration in the database and triggers
 * data reconciliation to clean up any stale data associated with previous sources.
 */
class UpdateLocationSourceUseCase @Inject constructor(
    private val locationsRepo: LocationsRepository,
    private val weatherDataReconcilerRepository: WeatherDataReconcilerRepository
) {
    /**
     * Updates the weather, air quality, and alert sources for a location.
     *
     * @return An in-memory copy of the updated [Location].
     */
    suspend operator fun invoke(
        location: Location,
        source: Source,
        airQualitySource: Source,
        alertSource: Source,
        openMeteoModel: OpenMeteoModel
    ): Location {
        val updatedLocation = location.copy(
            source = source,
            airQualitySource = airQualitySource,
            alertSource = alertSource,
            openMeteoModel = openMeteoModel
        )

        locationsRepo.updateSourceForLocation(location.id, source)
        locationsRepo.updateAirQualitySourceForLocation(location.id, airQualitySource)
        locationsRepo.updateAlertSourceForLocation(location.id, alertSource)
        locationsRepo.updateOpenMeteoModelForLocation(location.id, openMeteoModel)

        // Reconciliation cleans up stale data from previous sources.
        // Note: These updates are currently performed in sequence without a transaction.
        weatherDataReconcilerRepository.reconcileSourceChange(
            previous = location,
            updated = updatedLocation
        )
        
        return updatedLocation
    }
}