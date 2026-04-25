package com.pranshulgg.weathermaster.data.repository

import com.pranshulgg.weathermaster.core.model.WeatherResult
import com.pranshulgg.weathermaster.core.model.domain.Location
import com.pranshulgg.weathermaster.core.model.domain.Weather

interface WeatherRepository {
    suspend fun getWeather(location: Location, isRefresh: Boolean = false): WeatherResult

}