package com.pranshulgg.weathermaster.data.repository

import android.content.Context
import androidx.room.Transaction
import com.pranshulgg.weathermaster.core.model.domain.AppException
import com.pranshulgg.weathermaster.core.model.domain.Location
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.data.local.dao.WeatherLocationDao
import com.pranshulgg.weathermaster.data.local.mapper.toDomain
import com.pranshulgg.weathermaster.data.local.mapper.toEntity
import com.pranshulgg.weathermaster.data.provider.DeviceLocation
import com.pranshulgg.weathermaster.data.provider.GetDeviceLocation

import com.pranshulgg.weathermaster.feature.intro.toDomain
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

class LocationsRepository @Inject constructor(
    private val dao: WeatherLocationDao,
    @param:ApplicationContext private val context: Context
) {

    class Callbacks {
        fun onSuccess(cont: CancellableContinuation<DeviceLocation>, result: DeviceLocation) {
            if (cont.isActive) {
                cont.resumeWith(Result.success(result))
            }
        }

        fun onTimeout(cont: CancellableContinuation<DeviceLocation>) {
            if (cont.isActive) {
                cont.resumeWithException(AppException.CurrentLocationUnavailable())
            }
        }
    }

    private val callback = Callbacks()


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

    suspend fun isLocationsEmpty(): Boolean {
        val isEmpty = dao.getLocationsCount() == 0
        return isEmpty
    }

    @Transaction
    suspend fun updateDefaultLocation(id: String) {
        dao.clearDefaultLocations()
        dao.updateDefaultLocation(id)
    }

    fun getDefaultLocation(): Flow<Location?> {
        return dao.getDefaultLocation().map { it?.toDomain() }
    }

    suspend fun getWeatherForLocation(locationId: String): Weather {
        return dao.getWeatherForLocation(locationId).toDomain()
    }

    suspend fun updateDeviceLocationPosition() {

        val location = suspendCancellableCoroutine { cont ->
            GetDeviceLocation().getDeviceLocation(
                context,
                onTimeout = {
                    callback.onTimeout(cont)
                }) { result ->
                callback.onSuccess(cont, result)
            }
        }

        if (location.latitude == null || location.longitude == null) {
            return
        }

        val formattedLatitude = "%.5f".format(location.latitude).toDouble()
        val formattedLongitude = "%.5f".format(location.longitude).toDouble()

        dao.updateDeviceLocationPosition(formattedLatitude, formattedLongitude)
    }

    suspend fun saveDeviceLocation() {
        val location = suspendCancellableCoroutine { cont ->
            GetDeviceLocation().getDeviceLocation(
                context,
                onTimeout = {
                    callback.onTimeout(cont)
                }) { result ->
                callback.onSuccess(cont, result)
            }
        }
        if (location.latitude == null || location.longitude == null) {
            throw AppException.CurrentLocationUnavailable()
        }
        saveLocation(location.toDomain())
    }
}
