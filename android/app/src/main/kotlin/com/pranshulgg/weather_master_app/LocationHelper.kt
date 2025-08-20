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
    private var timeoutRunnable: Runnable? = null
    private var currentListener: LocationListener? = null

    companion object {
        private const val LOCATION_REQUEST_CODE = 999
        private const val TIMEOUT_MS = 30_000L // Extended to 30 seconds for microG
    }

    // Public method to get location
    fun getCurrentPosition(callback: LocationResult) {
        // Clean up any previous request
        cleanupLocationRequest()
        
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
            // Store callback and request permissions
            pendingCallback = callback
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
        
        // Check if location services are enabled
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && 
            !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            callback.onFailure("Location services are disabled")
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
                // Cancel timeout and clean up
                timeoutRunnable?.let { handler.removeCallbacks(it) }
                timeoutRunnable = null
                
                // Pass it on
                pendingCallback?.onSuccess(location.latitude, location.longitude)
                cleanupLocationRequest()
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
        
        currentListener = listener

        // Ask GPS and Network (network usually faster indoors)
        var requestCount = 0
        
        try {
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0L,
                    0f,
                    listener
                )
                requestCount++
            }
        } catch (e: Exception) {
            // Network provider failed, continue
        }
        
        try {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0L,
                    0f,
                    listener
                )
                requestCount++
            }
        } catch (e: Exception) {
            // GPS provider failed, continue
        }
        
        // If no providers could be started, fail immediately
        if (requestCount == 0) {
            callback.onFailure("No location providers available")
            return
        }

        // Step 3: Timeout in case nothing arrives
        timeoutRunnable = Runnable {
            pendingCallback?.onFailure("Location timeout - no location received within ${TIMEOUT_MS/1000} seconds")
            cleanupLocationRequest()
        }
        handler.postDelayed(timeoutRunnable!!, TIMEOUT_MS)
    }

    private fun cleanupLocationRequest() {
        currentListener?.let { listener ->
            try {
                locationManager.removeUpdates(listener)
            } catch (e: Exception) {
                // Ignore cleanup errors
            }
        }
        currentListener = null
        pendingCallback = null
        timeoutRunnable?.let { handler.removeCallbacks(it) }
        timeoutRunnable = null
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
                cleanupLocationRequest()
            }
        }
    }

    interface LocationResult {
        fun onSuccess(latitude: Double, longitude: Double)
        fun onFailure(error: String)
    }
}
