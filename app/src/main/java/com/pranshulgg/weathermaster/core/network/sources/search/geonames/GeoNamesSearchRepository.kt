package com.pranshulgg.weathermaster.core.network.sources.search.geonames

import com.pranshulgg.weathermaster.core.model.domain.location.Location
import com.pranshulgg.weathermaster.data.local.mapper.locations.toDomain
import com.pranshulgg.weathermaster.data.repository.SearchRepository
import javax.inject.Inject


class GeoNamesSearchRepository @Inject constructor(
    private val api: GeoNamesSearchApi
) : SearchRepository {
    override suspend fun search(query: String): List<Location> {

        val response = api.search(query)

        val body = response.body() ?: return emptyList()

        val domain = body.toDomain()

        return domain
    }


}