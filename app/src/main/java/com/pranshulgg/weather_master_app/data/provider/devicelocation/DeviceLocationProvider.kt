package com.pranshulgg.weather_master_app.data.provider.devicelocation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

data class DeviceLocation(
    val latitude: Double?,
    val longitude: Double?
)

class GetDeviceLocation {

    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null

    private var timeoutHandler: Handler? = null
    private var timeoutRunnable: Runnable? = null

    private val timeoutSecs = 10_000L // 10 seconds

    private fun getProvider(lm: LocationManager): String {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    lm.allProviders.contains(LocationManager.FUSED_PROVIDER) -> LocationManager.FUSED_PROVIDER

            lm.allProviders.contains(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            lm.allProviders.contains(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            else -> LocationManager.PASSIVE_PROVIDER
        }
    }

    fun getDeviceLocation(
        context: Context,
        onTimeout: () -> Unit,
        onResult: (DeviceLocation) -> Unit
    ) {


        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED


        if (!hasPermission) {
            onResult(DeviceLocation(null, null))
            return
        }

        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: run {
                onResult(DeviceLocation(null, null))
                return
            }

        locationManager = lm

        val provider = getProvider(lm)

        if (!lm.isProviderEnabled(provider)) {
            onResult(DeviceLocation(null, null))
            return
        }


        getLocation(onLocation = { onResult(it) }, lm, provider, onTimeout)
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun getLocation(
        onLocation: (DeviceLocation) -> Unit,
        lm: LocationManager,
        provider: String,
        onTimeout: () -> Unit
    ) {

        val lastKnown = lm.getLastKnownLocation(provider)

        if (lastKnown != null) {
            timeoutHandler?.removeCallbacks(timeoutRunnable!!)
            onLocation(
                DeviceLocation(
                    parseCord(lastKnown.latitude),
                    parseCord(lastKnown.longitude)
                )
            )
            return
        }

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                timeoutHandler?.removeCallbacks(timeoutRunnable!!)
                onLocation(
                    DeviceLocation(
                        parseCord(location.latitude),
                        parseCord(location.longitude)
                    )
                )
                stopUpdates()
            }

            override fun onProviderEnabled(provider: String) {}

            override fun onProviderDisabled(provider: String) {}
        }


        lm.requestLocationUpdates(
            provider,
            5000L,
            5f,
            locationListener!!
        )

        timeoutHandler = Handler(Looper.getMainLooper())

        timeoutRunnable = Runnable {
            stopUpdates()
            onTimeout()
        }

        timeoutHandler?.postDelayed(timeoutRunnable!!, timeoutSecs)
    }


    fun stopUpdates() {
        locationListener?.let { locationManager?.removeUpdates(it) }
        locationListener = null
    }
}


/**
 * Device might return lat/lon as a string based on the device locale
 * For e.g. "53,85893" -> app crashes, because upstream only takes in double
 * We convert that here
 */
private fun parseCord(value: Any?): Double? {
    return when (value) {
        is Double -> value
        is String -> value.replace(',', '.').toDouble()
        else -> null
    }
}