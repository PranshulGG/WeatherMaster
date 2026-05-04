package com.pranshulgg.weathermaster.data.provider

import android.Manifest
import android.content.Context
import android.location.LocationManager
import androidx.annotation.RequiresPermission

data class DeviceLocation(
    val latitude: Double?,
    val longitude: Double?
)

@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
fun getDeviceLocation(context: Context): DeviceLocation {
    val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return DeviceLocation(null, null)

    var lat: Double? = null
    var lon: Double? = null


    val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    val provider = when {
        hasGps -> LocationManager.GPS_PROVIDER
        hasNetwork -> LocationManager.NETWORK_PROVIDER
        else -> null
    }

    if (provider != null) {
        val location = locationManager.getLastKnownLocation(provider)

        location?.let {
            lat = it.latitude
            lon = it.longitude
        }

    }

    return DeviceLocation(lat, lon)

}