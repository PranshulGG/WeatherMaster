package com.pranshulgg.weather_master_app.data.repository

import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.weather.DistanceUnit
import com.pranshulgg.weather_master_app.core.model.weather.PrecipitationUnit
import com.pranshulgg.weather_master_app.core.model.weather.PressureUnit
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherUnitsDao
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toEntity
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class WeatherUnitsRepository @Inject constructor(
    private val dao: WeatherUnitsDao
) {


    suspend fun ensureDefaultExists() {
        if (dao.getOnce() == null) {
            dao.insert(WeatherUnits.getDefault().toEntity())
        }
    }

    fun getUnits(): Flow<WeatherUnits> = flow {
        ensureDefaultExists()
        emitAll(
            dao.getWeatherUnits().map { it!!.toDomain() }
        )
    }

    suspend fun getUnitsOnce(): WeatherUnits? {
        return dao.getWeatherUnitsOnce()?.toDomain()
    }


    suspend fun updateTemperatureUnit(tempUnit: TemperatureUnit) {
        dao.updateTemperatureUnit(tempUnit)
    }

    suspend fun updatePressureUnit(pressureUnit: PressureUnit) {
        dao.updatePressureUnit(pressureUnit)
    }

    suspend fun updateWindSpeedUnit(windSpeedUnit: WindSpeedUnit) {
        dao.updateWindSpeedUnit(windSpeedUnit)
    }

    suspend fun updateDistanceUnit(distanceUnit: DistanceUnit) {
        dao.updateDistanceUnit(distanceUnit)
    }

    suspend fun updatePrecipitationUnit(precipitationUnit: PrecipitationUnit) {
        dao.updatePrecipitationUnit(precipitationUnit)
    }

}