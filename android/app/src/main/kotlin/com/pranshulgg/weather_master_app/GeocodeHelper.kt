package com.pranshulgg.weather_master_app

import android.content.Context
import android.location.Geocoder

object GeocodeHelper {

    // Get city & country from latitude/longitude without freezing
    fun reverseGeocode(context: Context, latitude: Double, longitude: Double, callback: (Map<String, String>) -> Unit) {
        // Run this on a background thread because geocoding is slow-ish
        Thread {
            val geocoder = Geocoder(context)
            val result = try {
                // Ask Android to convert lat/lon → address
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)  // we just need the first match
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val city = address.locality ?: address.subAdminArea ?: address.adminArea ?: "Current"
                    val country = address.countryName ?: ""
                    mapOf("city" to city, "country" to country)
                } else {
                    mapOf("city" to "", "country" to "")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                mapOf("city" to "", "country" to "")
            }

            // Back to main thread so Flutter doesn't cry
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                callback(result)  // pass it back to whoever asked for it
            }
        }.start()
    }

}
