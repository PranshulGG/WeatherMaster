package com.pranshulgg.weathermaster.data.repository

import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.data.local.dao.WeatherDataDao
import com.pranshulgg.weathermaster.data.local.mapper.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class WeatherDataRepository @Inject constructor(
    private val weatherDataDao: WeatherDataDao
) {


    fun getAllLocationsWeather(): Flow<List<Weather>> {
        return weatherDataDao.getAllLocationsCurrentWeather()
            .map { list -> list.map { it.toDomain() } }
    }
}