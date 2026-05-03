package com.pranshulgg.weathermaster.data.repository

import com.pranshulgg.weathermaster.core.model.SearchProviders
import com.pranshulgg.weathermaster.core.model.domain.Location
import com.pranshulgg.weathermaster.core.network.search.geonames.GeoNamesSearchApi
import com.pranshulgg.weathermaster.core.network.search.geonames.GeoNamesTimezoneItem
import com.pranshulgg.weathermaster.core.network.search.openmeteo.OpenMeteoSearchApi
import com.pranshulgg.weathermaster.data.local.mapper.toDomain
import jakarta.inject.Inject

interface SearchRepository {
    suspend fun search(query: String): List<Location>

}
