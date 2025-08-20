package com.pranshulgg.weather_master_app

import android.content.Context
import android.location.Geocoder
import android.location.Geocoder.GeocodeListener
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import java.util.*

object GeocodeHelper {

    // Get city & country from latitude/longitude without freezing
    fun reverseGeocode(context: Context, latitude: Double, longitude: Double, callback: (Map<String, String>) -> Unit) {
        // Run this on a background thread because geocoding is slow-ish
        Thread {
            val geocoder = Geocoder(context)
            val result = try {
                // Check if Geocoder is available (might not work on some microG setups)
                if (!Geocoder.isPresent()) {
                    mapOf("city" to "Location", "country" to "Unknown")
                } else {
                    // Ask Android to convert lat/lon → address
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)  // we just need the first match
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        val city = address.locality ?: address.subAdminArea ?: address.adminArea ?: "Location"
                        val country = address.countryName ?: "Unknown"
                        mapOf("city" to city, "country" to country)
                    } else {
                        mapOf("city" to "Location", "country" to "Unknown")
                    }
                }
            } catch (e: Exception) {
                // Geocoding failed (common on microG), provide fallback
                mapOf("city" to "Location", "country" to "Unknown")
            }

            // Back to main thread so Flutter doesn't cry
            Handler(Looper.getMainLooper()).post {
                callback(result)  // pass it back to whoever asked for it
            }
        }.start()
    }

}
