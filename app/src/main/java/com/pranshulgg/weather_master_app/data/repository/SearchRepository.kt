package com.pranshulgg.weather_master_app.data.repository

import com.pranshulgg.weather_master_app.core.model.domain.location.Location

interface SearchRepository {
    suspend fun search(query: String): List<Location>

}
