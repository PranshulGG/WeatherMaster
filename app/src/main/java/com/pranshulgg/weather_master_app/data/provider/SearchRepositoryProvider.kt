package com.pranshulgg.weather_master_app.data.provider

import com.pranshulgg.weather_master_app.core.model.sources.SearchSource
import com.pranshulgg.weather_master_app.core.network.sources.search.accu.AccuSearchRepository
import com.pranshulgg.weather_master_app.core.network.sources.search.geonames.GeoNamesSearchRepository
import com.pranshulgg.weather_master_app.core.network.sources.search.openmeteo.OpenMeteoSearchRepository
import com.pranshulgg.weather_master_app.data.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryProvider @Inject constructor(
    private val openMeteoSearchRepository: OpenMeteoSearchRepository,
    private val geoNamesSearchRepository: GeoNamesSearchRepository,
    private val accuSearchRepository: AccuSearchRepository

) {

    fun getRepository(provider: SearchSource): SearchRepository {
        return when (provider) {
            SearchSource.OPEN_METEO -> openMeteoSearchRepository
            SearchSource.GEO_NAMES -> geoNamesSearchRepository
            SearchSource.ACCU_WEATHER -> accuSearchRepository
        }
    }

}