package com.pranshulgg.weather_master_app.data.repository

import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.nws.NwsDao
import jakarta.inject.Inject


class WeatherDataReconcilerRepository @Inject constructor(
    private val nwsDao: NwsDao,
    private val locationDao: LocationsDao
) {

    /**
     * This clears up extra data for sources not used
     * For e.g. when you switch from NWS to Open Meteo
     * NWS might have saved grid points
     * which are important to be removed from the DB so they don't end up stale
     */
    suspend fun cleanUpStaleData(previousSource: WeatherSource, locationId: String) {
        when (previousSource) {
            WeatherSource.NWS -> cleanNws(locationId)
            else -> {}
        }
    }

    suspend fun cleanNws(locationId: String) {
        nwsDao.deleteGridPointsForLocation(locationId)
    }
}