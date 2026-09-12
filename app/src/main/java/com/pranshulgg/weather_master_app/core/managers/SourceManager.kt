package com.pranshulgg.weather_master_app.core.managers

import com.pranshulgg.weather_master_app.core.managers.requests.PendingRequests
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.openmeteo.OpenMeteoModel
import com.pranshulgg.weather_master_app.data.local.dao.airquality.AirQualityDao
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.nws.NwsDao
import com.pranshulgg.weather_master_app.data.repository.WeatherContextRepository
import com.pranshulgg.weather_master_app.data.store.LocationStore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton


@Singleton
class SourceManager @Inject constructor(
    private val nwsDao: NwsDao,
    private val locationKeysDao: LocationKeysDao,
    private val airQualityDao: AirQualityDao,
    private val alertsDao: AlertsDao,
    private val weatherContextRepository: Provider<WeatherContextRepository>,
    private val pendingRequests: PendingRequests,
    private val locationStore: LocationStore
) {


    suspend fun updateSources(
        location: Location,
        source: Source,
        airQualitySource: Source,
        alertSource: Source,
        openMeteoModel: OpenMeteoModel
    ) {
        val updatedLocation = location.copy(
            source = source,
            airQualitySource = airQualitySource,
            alertSource = alertSource,
            openMeteoModel = openMeteoModel
        )

        val weatherContextRepository = weatherContextRepository.get()

        weatherContextRepository.updateSourceForLocation(location.id, source)
        weatherContextRepository.updateAirQualitySourceForLocation(location.id, airQualitySource)
        weatherContextRepository.updateAlertSourceForLocation(location.id, alertSource)
        weatherContextRepository.updateOpenMeteoModelForLocation(location.id, openMeteoModel)

        val forceRefreshForWeather = location.source != source
                || location.openMeteoModel != openMeteoModel
        val forceRefreshForAirQuality = location.airQualitySource != airQualitySource
        val forceRefreshForAlerts = location.alertSource != alertSource

        reconcileSourceChange(
            previous = location,
            updated = updatedLocation
        )
        locationStore.setLoading(true)
        locationStore.setActiveLocation(updatedLocation)
        pendingRequests.queueRequest(
            location = updatedLocation,
            isForceRefresh = forceRefreshForWeather,
            isForceRefreshForAirQuality = forceRefreshForAirQuality,
            isForceRefreshForAlerts = forceRefreshForAlerts
        )

    }


    /**
     * Removes cached data that is no longer needed after a location's
     * weather, air quality, or alert source changes.
     */
    suspend fun reconcileSourceChange(
        previous: Location,
        updated: Location
    ) {
        val locationId = previous.id

        if (previous.source != updated.source) {
            cleanWeatherData(
                locationId = locationId,
                previousSource = previous.source,
                airQualitySource = updated.airQualitySource,
                alertSource = updated.alertSource
            )
        }

        if (previous.airQualitySource != updated.airQualitySource) {
            cleanAirQualityData(
                locationId = locationId,
                currentWeatherSource = updated.source,
                currentAlertSource = updated.alertSource
            )
        }

        if (previous.alertSource != updated.alertSource) {
            cleanAlertsData(
                locationId = locationId,
                currentWeatherSource = updated.source,
                currentAirQualitySource = updated.airQualitySource
            )
        }
    }

    /**
     * Removes source-specific data that is no longer valid for the location.
     */
    suspend fun cleanLocationExtras(locationId: String) {
        nwsDao.deleteGridPointsForLocation(locationId)
        locationKeysDao.deleteCityKeyForLocation(locationId)
    }

    private suspend fun cleanWeatherData(
        locationId: String,
        previousSource: Source,
        airQualitySource: Source,
        alertSource: Source
    ) {
        when (previousSource) {
            Source.NWS -> {
                nwsDao.deleteGridPointsForLocation(locationId)
            }

            Source.ACCU_WEATHER -> {
                cleanAccuWeatherData(
                    locationId = locationId,
                    airQualitySource = airQualitySource,
                    alertSource = alertSource
                )
            }

            else -> Unit
        }
    }

    private suspend fun cleanAccuWeatherData(
        locationId: String,
        airQualitySource: Source,
        alertSource: Source
    ) {
        if (
            airQualitySource != Source.ACCU_WEATHER &&
            alertSource != Source.ACCU_WEATHER
        ) {
            locationKeysDao.deleteCityKeyForLocation(locationId)
        }
    }

    private suspend fun cleanAirQualityData(
        locationId: String,
        currentWeatherSource: Source,
        currentAlertSource: Source
    ) {
        airQualityDao.deleteCurrentAirQuality(locationId)
        airQualityDao.deleteHourlyAirQuality(locationId)

        if (
            currentWeatherSource != Source.ACCU_WEATHER &&
            currentAlertSource != Source.ACCU_WEATHER
        ) {
            locationKeysDao.deleteCityKeyForLocation(locationId)
        }
    }

    private suspend fun cleanAlertsData(
        locationId: String,
        currentWeatherSource: Source,
        currentAirQualitySource: Source
    ) {
        alertsDao.deleteAlertsForLocation(locationId)

        if (
            currentWeatherSource != Source.ACCU_WEATHER &&
            currentAirQualitySource != Source.ACCU_WEATHER
        ) {
            locationKeysDao.deleteCityKeyForLocation(locationId)
        }
    }
}