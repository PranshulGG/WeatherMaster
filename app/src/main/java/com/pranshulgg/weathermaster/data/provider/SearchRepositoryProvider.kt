package com.pranshulgg.weathermaster.data.provider

import com.pranshulgg.weathermaster.core.model.sources.SearchSource
import com.pranshulgg.weathermaster.core.network.search.geonames.GeoNamesSearchRepository
import com.pranshulgg.weathermaster.core.network.search.openmeteo.OpenMeteoSearchRepository
import com.pranshulgg.weathermaster.data.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryProvider @Inject constructor(
    private val openMeteoSearchRepository: OpenMeteoSearchRepository,
    private val geoNamesSearchRepository: GeoNamesSearchRepository

) {

    fun getRepository(provider: SearchSource): SearchRepository {
        return when (provider) {
            SearchSource.OPEN_METEO -> openMeteoSearchRepository
            SearchSource.GEO_NAMES -> geoNamesSearchRepository
        }
    }

}