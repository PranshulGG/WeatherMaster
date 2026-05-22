package com.pranshulgg.weather_master_app.data.repository

import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.weather.DistanceUnits
import com.pranshulgg.weather_master_app.core.model.weather.PrecipitationUnits
import com.pranshulgg.weather_master_app.core.model.weather.PressureUnits
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnits
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnits
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