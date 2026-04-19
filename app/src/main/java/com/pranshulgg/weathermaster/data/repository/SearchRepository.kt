package com.pranshulgg.weathermaster.data.repository

import com.pranshulgg.weathermaster.core.model.Location
import com.pranshulgg.weathermaster.core.network.search.OpenMeteoSearchApi
import com.pranshulgg.weathermaster.data.local.mapper.toDomain
import jakarta.inject.Inject

class SearchRepository @Inject constructor(
    private val api: OpenMeteoSearchApi
) {

    suspend fun search(query: String): List<Location> {

        val response = api.search(query)

        val body = response.body() ?: return emptyList()

        val domain = body.toDomain()

        return domain

    }


}