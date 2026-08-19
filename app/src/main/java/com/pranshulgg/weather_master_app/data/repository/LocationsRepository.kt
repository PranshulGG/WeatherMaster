package com.pranshulgg.weather_master_app.data.repository

import android.content.Context
import androidx.room.Transaction
import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.openmeteo.OpenMeteoModel
import com.pranshulgg.weather_master_app.core.network.sources.address.nominatim.json.NominatimRepository
import com.pranshulgg.weather_master_app.data.local.dao.airquality.AirQualityDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.mapper.airquality.toDomain
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
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


    suspend fun updateSourceForLocation(id: String, source: Source) {
        dao.updateSourceForLocation(id, source)
    }

    suspend fun updateOpenMeteoModelForLocation(id: String, model: OpenMeteoModel) {
        dao.updateOpenMeteoModelForLocation(id, model)
    }

    suspend fun updateAirQualitySourceForLocation(id: String, source: Source) {
        dao.updateAirQualitySourceForLocation(id, source)
    }

    suspend fun updateAlertSourceForLocation(id: String, source: Source) {
        dao.updateAlertSourceForLocation(id, source)
    }

    suspend fun getLocationForId(id: String): Location {
        return dao.getLocationForId(id).toDomain()
    }

    suspend fun updateLocationCustomName(id: String, name: String?) {
        dao.updateLocationCustomName(id, name)
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

    suspend fun getAirQualityForLocation(locationId: String): AirQuality? {
        return airQualityDao.getAirQualityForLocation(locationId)?.toDomain()
    }

    val getDeviceLocation = GetDeviceLocation()

    /**
     * Refreshes the saved device-location entry if the device has moved far enough.
     * Returns true if the location was actually updated (so callers know to force
     * a fresh weather fetch instead of trusting a now-stale cache).
     */
    suspend fun updateDeviceLocationPosition(): Boolean {

        val location = suspendCancellableCoroutine { cont ->
            getDeviceLocation.getDeviceLocation(
                context,
                onTimeout = {
                    callback.onTimeout(cont)
                }) { result ->
                callback.onSuccess(cont, result)
            }
        }


        val newLat = location.latitude ?: return false
        val newLon = location.longitude ?: return false

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
            return false
        }
        val address = try {
            nominatimRepository.getAddress(
                location.latitude,
                location.longitude
            )
        } catch (e: Exception) {
            null
        }


        val countryCode = if (address?.countryCode.isNullOrBlank()) {
            getCountryCode(context, location.latitude, location.longitude)
        } else {
            address.countryCode
        }

        dao.updateDeviceLocation(
            newLat,
            newLon,
            address?.city ?: currentLocation.name,
            address?.country ?: "",
            countryCode = countryCode ?: currentLocation.countryCode ?: "",
            ZoneId.systemDefault().id
        )

        return true
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


        val address = try {
            nominatimRepository.getAddress(
                location.latitude,
                location.longitude
            )
        } catch (e: Exception) {
            null
        }


        if (address != null && address.city != null) {

            val countryCode = if (address.countryCode.isNullOrBlank()) {
                getCountryCode(context, location.latitude, location.longitude)
            } else {
                address.countryCode
            }

            saveLocation(
                location.toDomain(context).copy(
                    name = address.city,
                    country = address.country,
                    countryCode = countryCode
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


