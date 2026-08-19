package com.pranshulgg.weather_master_app.data.repository

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.data.local.dao.airquality.AirQualityDao
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.nws.NwsDao
import jakarta.inject.Inject


class WeatherDataReconcilerRepository @Inject constructor(
    private val nwsDao: NwsDao,
    private val locationDao: LocationsDao,
    private val locationKeysDao: LocationKeysDao,
    private val airQualityDao: AirQualityDao,
    private val alertsDao: AlertsDao
) {

    /**
     * This clears up extra data for sources not used
     * For e.g. when you switch from NWS to Open Meteo
     * NWS might have saved grid points
     * which are important to be removed from the DB so they don't end up stale
     */
    suspend fun reconcileSourceChange(
        previous: Location,
        updated: Location
    ) {
        if (previous.source != updated.source) {
            cleanUpStaleWeatherData(
                previousSource = previous.source,
                locationId = previous.id,
                airQualitySource = updated.airQualitySource,
                currentAlertSource = updated.alertSource
            )
        }

        if (previous.airQualitySource != updated.airQualitySource) {
            cleanUpStaleAirQualityData(
                locationId = previous.id,
                currentWeatherSource = updated.source,
                currentAlertSource = updated.alertSource
            )
        }

        if (previous.alertSource != updated.alertSource) {
            cleanUpStaleAlertsData(
                locationId = previous.id,
                currentWeatherSource = updated.source,
                airQualitySource = updated.airQualitySource
            )
        }
    }

    private suspend fun cleanUpStaleWeatherData(
        previousSource: Source,
        locationId: String,
        airQualitySource: Source,
        currentAlertSource: Source
    ) {
        when (previousSource) {
            Source.NWS -> nwsDao.deleteGridPointsForLocation(locationId)

            Source.ACCU_WEATHER -> cleanAccuWeather(
                locationId,
                airQualitySource,
                currentAlertSource
            )

            else -> {}
        }
    }

    private suspend fun cleanAccuWeather(
        locationId: String, airQualitySource: Source,
        currentAlertSource: Source
    ) {
        if (airQualitySource != Source.ACCU_WEATHER || currentAlertSource != Source.ACCU_WEATHER) {
            locationKeysDao.deleteCityKeyForLocation(locationId)
        }
    }

    private suspend fun cleanUpStaleAirQualityData(
        locationId: String,
        currentWeatherSource: Source,
        currentAlertSource: Source
    ) {
        airQualityDao.deleteCurrentAirQuality(locationId)
        airQualityDao.deleteHourlyAirQuality(locationId)

        if (currentWeatherSource != Source.ACCU_WEATHER || currentAlertSource != Source.ACCU_WEATHER) {
            locationKeysDao.deleteCityKeyForLocation(locationId)
        }
    }

    private suspend fun cleanUpStaleAlertsData(
        locationId: String,
        currentWeatherSource: Source,
        airQualitySource: Source,
    ) {
        alertsDao.deleteAlertsForLocation(locationId)

        if (currentWeatherSource != Source.ACCU_WEATHER || airQualitySource != Source.ACCU_WEATHER) {
            locationKeysDao.deleteCityKeyForLocation(locationId)
        }
    }

}