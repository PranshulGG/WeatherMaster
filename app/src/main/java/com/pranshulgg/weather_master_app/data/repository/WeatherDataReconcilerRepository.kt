package com.pranshulgg.weather_master_app.data.repository

import com.pranshulgg.weather_master_app.core.model.sources.AirQualitySource
import com.pranshulgg.weather_master_app.core.model.sources.AlertSource
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.data.local.dao.airquality.AirQualityDao
import com.pranshulgg.weather_master_app.data.local.dao.airquality.accu.AccuDao
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.nws.NwsDao
import jakarta.inject.Inject


class WeatherDataReconcilerRepository @Inject constructor(
    private val nwsDao: NwsDao,
    private val locationDao: LocationsDao,
    private val accuDao: AccuDao,
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

            else -> {}
        }
    }

    suspend fun cleanAccuWeather(
        locationId: String, airQualitySource: AirQualitySource,
        currentAlertSource: AlertSource
    ) {
        if (airQualitySource != AirQualitySource.ACCU_WEATHER || currentAlertSource != AlertSource.ACCU_WEATHER) {
            accuDao.deleteCityKeyForLocation(locationId)
        }
    }

    suspend fun cleanUpStaleAirQualityData(
        currentSource: AirQualitySource,
        locationId: String,
        currentWeatherSource: WeatherSource,
        currentAlertSource: AlertSource
    ) {
        if (currentSource == AirQualitySource.NONE) {
            airQualityDao.deleteCurrentAirQuality(locationId)
            airQualityDao.deleteHourlyAirQuality(locationId)
        }

        if (currentWeatherSource != WeatherSource.ACCU_WEATHER || currentAlertSource != AlertSource.ACCU_WEATHER) {
            accuDao.deleteCityKeyForLocation(locationId)
        }
    }

    suspend fun cleanUpStaleAlertsData(
        currentSource: AlertSource,
        locationId: String,
        currentWeatherSource: WeatherSource,
        airQualitySource: AirQualitySource,
    ) {
        if (currentSource == AlertSource.NONE) {
            alertsDao.deleteAlertsForLocation(locationId)
        }

        if (currentWeatherSource != WeatherSource.ACCU_WEATHER || airQualitySource != AirQualitySource.ACCU_WEATHER) {
            accuDao.deleteCityKeyForLocation(locationId)
        }
    }

}