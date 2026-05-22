package com.pranshulgg.weather_master_app.core.network.sources.search.geonames

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.data.local.mapper.locations.toDomain
import com.pranshulgg.weather_master_app.data.repository.SearchRepository
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