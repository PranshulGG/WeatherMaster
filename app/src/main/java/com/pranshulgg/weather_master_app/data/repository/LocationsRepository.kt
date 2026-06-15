package com.pranshulgg.weather_master_app.data.repository

import android.content.Context
import android.util.Log
import androidx.room.Transaction
import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.network.sources.address.nominatim.json.NominatimRepository
import com.pranshulgg.weather_master_app.data.local.dao.airquality.AirQualityDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.mapper.locations.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.locations.toEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDomain
import com.pranshulgg.weather_master_app.data.provider.devicelocation.DeviceLocation
import com.pranshulgg.weather_master_app.data.provider.devicelocation.GetDeviceLocation
import com.pranshulgg.weather_master_app.data.provider.devicelocation.getCountryCode
import com.pranshulgg.weather_master_app.feature.intro.toDomain
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.time.ZoneId
import kotlin.coroutines.resumeWithException

class LocationsRepository @Inject constructor(
    private val dao: LocationsDao,
    private val airQualityDao: AirQualityDao,
    @param:ApplicationContext private val context: Context,
    private val nominatimRepository: NominatimRepository
) {
    private val LOCATION_UPDATE_THRESHOLD_METERS = 1000f // 1000m

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

    suspend fun getLocationsOnce(): List<Location> {
        return dao.getLocationsOnce().map { it.toDomain() }
    }

    @Transaction
    suspend fun deleteLocation(id: String) {
        dao.deleteLocation(id)
        airQualityDao.deleteCurrentAirQuality(id)
    }

    suspend fun updateSourceForLocation(id: String, source: WeatherSource) {
        dao.updateSourceForLocation(id, source)
    }


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

    val getDeviceLocation = GetDeviceLocation()

    suspend fun updateDeviceLocationPosition() {

        val location = suspendCancellableCoroutine { cont ->
            getDeviceLocation.getDeviceLocation(
                context,
                onTimeout = {
                    callback.onTimeout(cont)
                }) { result ->
                callback.onSuccess(cont, result)
            }
        }


        val newLat = location.latitude ?: return
        val newLon = location.longitude ?: return

        val currentLocation = dao.getDeviceLocation()


        val results = FloatArray(1)

        android.location.Location.distanceBetween(
            currentLocation.lat,
            currentLocation.lon,
            newLat,
            newLon,
            results
        )

        val distanceInMeters = results[0]
        // Only update the location if needed
        if (distanceInMeters < LOCATION_UPDATE_THRESHOLD_METERS) {
            return
        }

        val address = nominatimRepository.getAddress(location.latitude, location.longitude)

        dao.updateDeviceLocation(
            newLat,
            newLon,
            address?.city ?: "$newLat, $newLon",
            address?.country ?: "",
            address?.countryCode ?: getCountryCode(context, location.latitude, location.longitude)
            ?: "",
            ZoneId.systemDefault().id
        )
    }

    suspend fun saveDeviceLocation() {

        val location = suspendCancellableCoroutine { cont ->
            getDeviceLocation.getDeviceLocation(
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


        val address = nominatimRepository.getAddress(location.latitude, location.longitude)

        if (address != null && address.city != null) {
            saveLocation(
                location.toDomain(context).copy(
                    name = address.city,
                    country = address.country,
                    countryCode = address.countryCode
                )
            )
        } else {
            saveLocation(
                location.toDomain(context)
            )
        }
    }

    fun getWeatherForAllLocations(): Flow<List<Weather>> {
        return dao.getAllLocationsCurrentWeather()
            .map { list -> list.map { it.toDomain() } }
    }
}


