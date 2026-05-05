package com.pranshulgg.weathermaster.data.repository

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.room.Transaction
import com.pranshulgg.weathermaster.core.model.domain.Location
import com.pranshulgg.weathermaster.data.local.dao.WeatherLocationDao
import com.pranshulgg.weathermaster.data.local.mapper.toDomain
import com.pranshulgg.weathermaster.data.local.mapper.toEntity
import com.pranshulgg.weathermaster.data.provider.getDeviceLocation
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocationsRepository @Inject constructor(
    private val dao: WeatherLocationDao,
    @param:ApplicationContext private val context: Context
) {

    fun getLocations(): Flow<List<Location>> {
        return dao.getLocations().map { it.toDomain() }
    }

    suspend fun deleteLocation(id: String) = dao.deleteLocation(id)


    @Transaction
    suspend fun saveLocation(location: Location) {
        val isFirst = dao.getLocationsCount() == 0
        dao.insertWeatherLocation(location.toEntity())

        if (isFirst) {
            updateDefaultLocation(location.id)
        }

    }

    @Transaction
    suspend fun updateDefaultLocation(id: String) {
        dao.clearDefaultLocations()
        dao.updateDefaultLocation(id)
    }

    fun getDefaultLocation(): Flow<Location?> {
        return dao.getDefaultLocation().map { it?.toDomain() }
    }

    suspend fun updateDeviceLocationPosition() {
        val location = getDeviceLocation(context)

        if (location.latitude == null || location.longitude == null) return

        val formattedLatitude = "%.5f".format(location.latitude).toDouble()
        val formattedLongitude = "%.5f".format(location.longitude).toDouble()

        dao.updateDeviceLocationPosition(formattedLatitude, formattedLongitude)
    }
}
