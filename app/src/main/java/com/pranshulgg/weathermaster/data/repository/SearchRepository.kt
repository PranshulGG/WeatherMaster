package com.pranshulgg.weathermaster.data.repository

import com.pranshulgg.weathermaster.core.model.domain.location.Location

interface SearchRepository {
    suspend fun search(query: String): List<Location>

}
