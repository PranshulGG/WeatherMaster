package com.pranshulgg.weathermaster.data.repository

import com.pranshulgg.weathermaster.core.model.weather.DistanceUnits
import com.pranshulgg.weathermaster.core.model.weather.PrecipitationUnits
import com.pranshulgg.weathermaster.core.model.weather.PressureUnits
import com.pranshulgg.weathermaster.core.model.weather.TemperatureUnits
import com.pranshulgg.weathermaster.core.model.weather.WindSpeedUnits
import com.pranshulgg.weathermaster.core.model.domain.AppWeatherUnits
import com.pranshulgg.weathermaster.data.local.dao.AppWeatherUnitsDao
import com.pranshulgg.weathermaster.data.local.mapper.toDomain
import com.pranshulgg.weathermaster.data.local.mapper.toEntity
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class AppWeatherUnitsRepository @Inject constructor(
    private val dao: AppWeatherUnitsDao
) {


    suspend fun ensureDefaultExists() {
        if (dao.getOnce() == null) {
            dao.insert(AppWeatherUnits.getDefault().toEntity())
        }
    }

    fun getUnits(): Flow<AppWeatherUnits> = flow {
        ensureDefaultExists()
        emitAll(
            dao.getWeatherUnits().map { it!!.toDomain() }
        )
    }


    suspend fun updateTemperatureUnit(tempUnit: TemperatureUnits) {
        dao.updateTemperatureUnit(tempUnit)
    }

    suspend fun updatePressureUnit(pressureUnit: PressureUnits) {
        dao.updatePressureUnit(pressureUnit)
    }

    suspend fun updateWindSpeedUnit(windSpeedUnit: WindSpeedUnits) {
        dao.updateWindSpeedUnit(windSpeedUnit)
    }

    suspend fun updateDistanceUnit(distanceUnit: DistanceUnits) {
        dao.updateDistanceUnit(distanceUnit)
    }

    suspend fun updatePrecipitationUnit(precipitationUnit: PrecipitationUnits) {
        dao.updatePrecipitationUnit(precipitationUnit)
    }

}