package com.pranshulgg.weathermaster.data.provider

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat

data class DeviceLocation(
    val latitude: Double?,
    val longitude: Double?
)

fun getDeviceLocation(context: Context): DeviceLocation {

    val hasPermission =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

    if (!hasPermission) {
        return DeviceLocation(null, null)
    }

    val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return DeviceLocation(null, null)

    var lat: Double? = null
    var lon: Double? = null


    val providers = listOf(
        LocationManager.NETWORK_PROVIDER,
        LocationManager.GPS_PROVIDER
    )

    for (p in providers) {
        if (locationManager.isProviderEnabled(p)) {
            val loc = locationManager.getLastKnownLocation(p)
            if (loc != null) {
                lat = loc.latitude
                lon = loc.longitude
                break
            }
        }
    }



    return DeviceLocation(lat, lon)

}