package com.pranshulgg.weather_master_app.data.repository

import com.pranshulgg.weather_master_app.core.model.sources.AirQualitySource
import com.pranshulgg.weather_master_app.core.model.sources.AlertSource
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
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
    suspend fun cleanUpStaleData(
        previousSource: WeatherSource,
        locationId: String,
        airQualitySource: AirQualitySource,
        currentAlertSource: AlertSource
    ) {
        when (previousSource) {
            WeatherSource.NWS -> nwsDao.deleteGridPointsForLocation(locationId)
            WeatherSource.ACCU_WEATHER -> cleanAccuWeather(
                locationId,
                airQualitySource,
                currentAlertSource
            )

            WeatherSource.IPMA -> locationKeysDao.getCityKeyForLocation(locationId)
            else -> {}
        }
    }

    private suspend fun cleanAccuWeather(
        locationId: String, airQualitySource: AirQualitySource,
        currentAlertSource: AlertSource
    ) {
        if (airQualitySource != AirQualitySource.ACCU_WEATHER || currentAlertSource != AlertSource.ACCU_WEATHER) {
            locationKeysDao.deleteCityKeyForLocation(locationId)
        }
    }

    suspend fun cleanUpStaleAirQualityData(
        locationId: String,
        currentWeatherSource: WeatherSource,
        currentAlertSource: AlertSource
    ) {
        airQualityDao.deleteCurrentAirQuality(locationId)
        airQualityDao.deleteHourlyAirQuality(locationId)

        if (currentWeatherSource != WeatherSource.ACCU_WEATHER || currentAlertSource != AlertSource.ACCU_WEATHER) {
            locationKeysDao.deleteCityKeyForLocation(locationId)
        }
    }

    suspend fun cleanUpStaleAlertsData(
        locationId: String,
        currentWeatherSource: WeatherSource,
        airQualitySource: AirQualitySource,
    ) {
        alertsDao.deleteAlertsForLocation(locationId)

        if (currentWeatherSource != WeatherSource.ACCU_WEATHER || airQualitySource != AirQualitySource.ACCU_WEATHER) {
            locationKeysDao.deleteCityKeyForLocation(locationId)
        }
    }

}