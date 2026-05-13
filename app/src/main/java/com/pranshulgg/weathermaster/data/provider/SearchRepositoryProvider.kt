package com.pranshulgg.weathermaster.data.provider

import com.pranshulgg.weathermaster.core.model.providers.SearchProviders
import com.pranshulgg.weathermaster.core.network.search.geonames.GeoNamesSearchRepository
import com.pranshulgg.weathermaster.core.network.search.openmeteo.OpenMeteoSearchRepository
import com.pranshulgg.weathermaster.data.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryProvider @Inject constructor(
    private val openMeteoSearchRepository: OpenMeteoSearchRepository,
    private val geoNamesSearchRepository: GeoNamesSearchRepository

) {

    fun getRepository(provider: SearchProviders): SearchRepository {
        return when (provider) {
            SearchProviders.OPEN_METEO -> openMeteoSearchRepository
            SearchProviders.GEO_NAMES -> geoNamesSearchRepository
        }
    }

}