package com.pranshulgg.weathermaster.data.repository

import androidx.room.Transaction
import com.pranshulgg.weathermaster.core.model.domain.Location
import com.pranshulgg.weathermaster.core.ui.state.ActiveLocationStore
import com.pranshulgg.weathermaster.data.local.dao.WeatherLocationDao
import com.pranshulgg.weathermaster.data.local.mapper.toEntity
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import com.pranshulgg.weathermaster.data.local.mapper.toDomain

class LocationsRepository @Inject constructor(
    private val dao: WeatherLocationDao,
    private val activeLocationStore: ActiveLocationStore
) {

    fun getLocations(): Flow<List<Location>> {
        return dao.getLocations().map { it.toDomain() }
    }

    suspend fun deleteLocation(id: String) = dao.deleteLocation(id)


    @Transaction
    suspend fun saveLocation(location: Location) {
        val isFirst = dao.getLocationsCount() == 0
        dao.insertWeatherLocation(location.toEntity())

        if (isFirst) {
            updateDefaultLocation(location.id)
        }

        activeLocationStore.set(location)
    }

    suspend fun updateDefaultLocation(id: String) {
        dao.updateDefaultLocation(id)
    }

    fun getDefaultLocation(): Flow<Location?> {
        return dao.getDefaultLocation().map { it?.toDomain() }
    }

}
