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

    private val timeoutMillis = 20_000L

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

        getLocation(onLocation = { onResult(it) }, lm, onTimeout)
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun getLocation(
        onLocation: (DeviceLocation) -> Unit,
        lm: LocationManager,
        onTimeout: () -> Unit
    ) {

//        val lastKnown = lm.getLastKnownLocation(provider)
//            ?: lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//            ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
//            ?: lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
//

        val lastKnown = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        ).filter { lm.allProviders.contains(it) }.mapNotNull { provider ->
            try {
                lm.getLastKnownLocation(provider)
            } catch (e: SecurityException) {
                null
            }
        }.maxByOrNull { it.time }


        if (lastKnown != null) {
            onLocation(
                DeviceLocation(
                    parseCord(lastKnown.latitude),
                    parseCord(lastKnown.longitude)
                )
            )
            return
        }

        var delivered = false

        fun deliver(location: Location) {
            if (delivered) return

            delivered = true
            stopUpdates()

            onLocation(
                DeviceLocation(
                    parseCord(location.latitude),
                    parseCord(location.longitude)
                )
            )
        }

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                deliver(location)
            }

            override fun onProviderEnabled(provider: String) {}

            override fun onProviderDisabled(provider: String) {}
        }


        val providers = listOf(
            LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER
        ).filter {
            lm.allProviders.contains(it) && runCatching { lm.isProviderEnabled(it) }.getOrDefault(
                false
            )
        }

        if (providers.isEmpty()) {
            locationListener = null
            onTimeout()
            return
        }

        // request live updates from BOTH providers @reveler-hub #938
        providers.forEach { provider ->
            try {
                lm.requestLocationUpdates(
                    provider,
                    0L,
                    0f,
                    locationListener!!,
                    Looper.getMainLooper()
                )
            } catch (_: SecurityException) {
            }
        }


        timeoutHandler = Handler(Looper.getMainLooper())

        timeoutRunnable = Runnable {
            if (!delivered) {
                stopUpdates()
                onTimeout()
            }
        }

        timeoutHandler?.postDelayed(
            timeoutRunnable!!,
            timeoutMillis
        )
    }


    fun stopUpdates() {
        timeoutRunnable?.let {
            timeoutHandler?.removeCallbacks(it)
        }

        timeoutRunnable = null
        timeoutHandler = null

        locationListener?.let { listener ->
            locationManager?.removeUpdates(listener)
        }

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