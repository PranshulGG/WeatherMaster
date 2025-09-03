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
    private var listener: LocationListener? = null
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private const val LOCATION_REQUEST_CODE = 999
        private const val TIMEOUT_MS = 60_000L 
    }

    fun getCurrentPosition(callback: LocationResult) {
        pendingCallback = callback

        val hasFine = ActivityCompat.checkSelfPermission(
            activity, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarse = ActivityCompat.checkSelfPermission(
            activity, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFine && !hasCoarse) {
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

        val lastLocation = when {
            hasFine -> locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            hasCoarse -> locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            else -> null
        } ?: locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)

        lastLocation?.let {
            callback.onSuccess(it.latitude, it.longitude)
            return
        }

        val providersToUse = mutableListOf<String>()
        if (hasFine && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            providersToUse.add(LocationManager.GPS_PROVIDER)
        }
        if (hasCoarse && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            providersToUse.add(LocationManager.NETWORK_PROVIDER)
        }
        if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            providersToUse.add(LocationManager.PASSIVE_PROVIDER)
        }

        if (providersToUse.isEmpty()) {
            callback.onFailure("No location provider available for granted permission")
            return
        }

        listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                Log.d("LocationHelper", "Location received: ${location.latitude}, ${location.longitude}")
                pendingCallback?.onSuccess(location.latitude, location.longitude)
                removeUpdates()
            }

            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }

        providersToUse.forEach { provider ->
            try {
                locationManager.requestLocationUpdates(
                    provider,
                    1000L,
                    0f,
                    listener!!
                )
            } catch (e: SecurityException) {
                Log.e("LocationHelper", "Permission issue for $provider: $e")
            } catch (e: Exception) {
                Log.e("LocationHelper", "Failed to request updates for $provider: $e")
            }
        }

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


    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode != LOCATION_REQUEST_CODE) return

        val hasFine = ActivityCompat.checkSelfPermission(
            activity, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarse = ActivityCompat.checkSelfPermission(
            activity, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val callback = pendingCallback
        pendingCallback = null

        if (hasFine || hasCoarse) {
            callback?.let { getCurrentPosition(it) }
        } else {
            callback?.onFailure("Location permission denied")
        }
    }

    interface LocationResult {
        fun onSuccess(latitude: Double, longitude: Double)
        fun onFailure(error: String)
    }
}
