package com.pranshulgg.weather_master_app.core.network.sources.address.nominatim.json

import com.pranshulgg.weather_master_app.core.model.domain.location.Address
import com.pranshulgg.weather_master_app.core.network.sources.address.nominatim.NominatimApi
import com.pranshulgg.weather_master_app.data.local.mapper.locations.toDomain
import java.net.UnknownHostException
import javax.inject.Inject

class NominatimRepository @Inject constructor(
    private val api: NominatimApi
) {
    suspend fun getAddress(latitude: Double?, longitude: Double?): Address? {
        try {

            if (latitude == null || longitude == null) return null

            val response = api.reverseGeocode("jsonv2", latitude, longitude)

            val body = response.body() ?: throw UnknownHostException()

            val domain = body.toDomain()

            return domain

        } catch (e: Exception) {
            throw e
        }
    }
}