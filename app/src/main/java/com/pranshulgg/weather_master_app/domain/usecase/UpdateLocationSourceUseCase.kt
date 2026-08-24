package com.pranshulgg.weather_master_app.domain.usecase

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.weather.openmeteo.OpenMeteoModel
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.data.repository.LocationsRepository
import com.pranshulgg.weather_master_app.data.repository.WeatherDataReconcilerRepository
import javax.inject.Inject

class UpdateLocationSourceUseCase @Inject constructor(
    private val locationsRepo: LocationsRepository,
    private val weatherDataReconcilerRepository: WeatherDataReconcilerRepository
) {
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

        weatherDataReconcilerRepository.reconcileSourceChange(
            previous = location,
            updated = updatedLocation
        )
        
        return updatedLocation
    }
}