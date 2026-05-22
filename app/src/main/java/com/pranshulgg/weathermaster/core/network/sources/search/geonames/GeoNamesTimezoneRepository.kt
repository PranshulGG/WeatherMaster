package com.pranshulgg.weathermaster.core.network.sources.search.geonames

import com.pranshulgg.weathermaster.core.network.sources.search.geonames.json.GeoNamesTimezoneItem
import com.pranshulgg.weathermaster.core.network.sources.search.geonames.json.toDomain
import com.pranshulgg.weathermaster.core.network.sources.search.geonames.timezone.GeoNamesTimezoneApi
import jakarta.inject.Inject


class GeoNamesTimezoneRepository @Inject constructor(private val api: GeoNamesTimezoneApi) {

    suspend fun getTimeZone(latitude: Double, longitude: Double): GeoNamesTimezoneItem? {
        val response = api.getTimezone(latitude, longitude)

        val body = response.body() ?: return null

        val domain = body.toDomain()

        return domain
    }
}