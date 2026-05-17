package com.pranshulgg.weathermaster.data.provider

import com.pranshulgg.weathermaster.core.model.providers.SearchProvider
import com.pranshulgg.weathermaster.core.network.search.geonames.GeoNamesSearchRepository
import com.pranshulgg.weathermaster.core.network.search.openmeteo.OpenMeteoSearchRepository
import com.pranshulgg.weathermaster.data.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryProvider @Inject constructor(
    private val openMeteoSearchRepository: OpenMeteoSearchRepository,
    private val geoNamesSearchRepository: GeoNamesSearchRepository

) {

    fun getRepository(provider: SearchProvider): SearchRepository {
        return when (provider) {
            SearchProvider.OPEN_METEO -> openMeteoSearchRepository
            SearchProvider.GEO_NAMES -> geoNamesSearchRepository
        }
    }

}