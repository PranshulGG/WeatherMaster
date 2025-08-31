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
import android.util.Log
import androidx.core.app.ActivityCompat

class LocationHelper(private val activity: Activity) {

    private val locationManager =
        activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private var pendingCallback: LocationResult? = null
    private val handler = Handler(Looper.getMainLooper())
    private var listener: LocationListener? = null

    companion object {
        private const val LOCATION_REQUEST_CODE = 999
        private const val TIMEOUT_MS = 60_000L // 60 seconds for slower fixes
    }

    fun getCurrentPosition(callback: LocationResult) {
        pendingCallback = callback

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
            // Request permission if missing
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_REQUEST_CODE
            )
            return
        }

        // Step 1: Try last known location first
        val lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)

        if (lastLocation != null) {
            callback.onSuccess(lastLocation.latitude, lastLocation.longitude)
            return
        }

        // Step 2: Check available providers
        val providers = mutableListOf<String>()
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) providers.add(LocationManager.GPS_PROVIDER)
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) providers.add(LocationManager.NETWORK_PROVIDER)
        if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) providers.add(LocationManager.PASSIVE_PROVIDER)

        if (providers.isEmpty()) {
            callback.onFailure("No location provider enabled")
            return
        }

        // Step 3: Set up listener
        listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                Log.d("LocationHelper", "Location received: ${location.latitude}, ${location.longitude}")
                pendingCallback?.onSuccess(location.latitude, location.longitude)
                removeUpdates()
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        // Request updates from all available providers
        providers.forEach { provider ->
            try {
                locationManager.requestLocationUpdates(
                    provider,
                    1000L, // 1-second interval
                    0f,
                    listener!!
                )
            } catch (e: SecurityException) {
                Log.e("LocationHelper", "Permission issue: $e")
            } catch (e: Exception) {
                Log.e("LocationHelper", "Failed to request updates for $provider: $e")
            }
        }

        // Step 4: Timeout handler
        handler.postDelayed({
            pendingCallback?.onFailure("Location timeout")
            removeUpdates()
        }, TIMEOUT_MS)
    }

    private fun removeUpdates() {
        listener?.let { locationManager.removeUpdates(it) }
        listener = null
        pendingCallback = null
    }

    // Forward Activity permission result
    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            val callback = pendingCallback
            pendingCallback = null
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callback?.let { getCurrentPosition(it) }
            } else {
                callback?.onFailure("Location permission denied")
            }
        }
    }

    interface LocationResult {
        fun onSuccess(latitude: Double, longitude: Double)
        fun onFailure(error: String)
    }
}
