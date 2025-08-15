package com.pranshulgg.weather_master_app

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat

class LocationHelper(private val activity: Activity) {

    private val locationManager =
        activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    // Talks to GPS & network

    private var pendingCallback: LocationResult? = null
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private const val LOCATION_REQUEST_CODE = 999
        private const val TIMEOUT_MS = 10_000L // Max wait 10 seconds
    }

    // Public method to get location
    fun getCurrentPosition(callback: LocationResult) {
        // Step 0: Permission check
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Step 1: Try last known location first (fastest route)
        val lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        if (lastLocation != null) {
            callback.onSuccess(lastLocation.latitude, lastLocation.longitude)
            return
        }

        // Step 2: Set up listener for updates
        pendingCallback = callback
        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // Pass it on
                pendingCallback?.onSuccess(location.latitude, location.longitude)
                removeUpdates(this)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        // Ask GPS and Network (network usually faster indoors)
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0L,
                0f,
                listener
            )
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0L,
                0f,
                listener
            )
        }

        // Step 3: Timeout in case nothing arrives
        handler.postDelayed({
            pendingCallback?.onFailure("Location timeout")
            removeUpdates(listener)
        }, TIMEOUT_MS)
    }

    private fun removeUpdates(listener: LocationListener) {
        locationManager.removeUpdates(listener)
        pendingCallback = null
    }

    // Forward Activity permission result
    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            val callback = pendingCallback
            pendingCallback = null

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // User said yes → fetch location
                callback?.let { getCurrentPosition(it) }
            } else {
                // User said no → sad face
                callback?.onFailure("Location permission denied")
            }
        }
    }

    interface LocationResult {
        fun onSuccess(latitude: Double, longitude: Double)
        fun onFailure(error: String)
    }
}
