package com.pranshulgg.weather_master_app

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Handler
import android.os.Looper

object GeocodeHelper {

    fun reverseGeocode(
        context: Context,
        latitude: Double,
        longitude: Double,
        callback: (Map<String, String>) -> Unit
    ) {
        val mainHandler = Handler(Looper.getMainLooper())
        val geocoder = Geocoder(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            geocoder.getFromLocation(
                latitude,
                longitude,
                1,
                object : Geocoder.GeocodeListener {

                    override fun onGeocode(addresses: MutableList<Address>) {
                        val address = addresses.firstOrNull()

                        val city =
                            address?.locality
                                ?: address?.subLocality
                                ?: address?.subAdminArea
                                ?: address?.adminArea
                                ?: address?.featureName
                                ?: "Current Location"

                        val country =
                            address?.countryName
                                ?: address?.countryCode
                                ?: ""

                        mainHandler.post {
                            callback(mapOf("city" to city, "country" to country))
                        }
                    }

                    override fun onError(errorMessage: String?) {
                        mainHandler.post {
                            callback(
                                mapOf(
                                    "city" to "Current Location",
                                    "country" to ""
                                )
                            )
                        }
                    }
                }
            )

        } else {
            Thread {
                val result = try {
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)

                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]

                        val city =
                            address.locality
                                ?: address.subLocality
                                ?: address.subAdminArea
                                ?: address.adminArea
                                ?: address.featureName
                                ?: "Current Location"

                        val country =
                            address.countryName
                                ?: address.countryCode
                                ?: ""

                        mapOf("city" to city, "country" to country)
                    } else {
                        mapOf("city" to "Current Location", "country" to "")
                    }
                } catch (e: Exception) {
                    mapOf("city" to "Current Location", "country" to "")
                }

                mainHandler.post {
                    callback(result)
                }
            }.start()
        }
    }
}
