package com.pranshulgg.weathermaster.data.repository

import com.pranshulgg.weathermaster.core.model.Location
import com.pranshulgg.weathermaster.core.model.Weather

interface WeatherRepository {
    suspend fun getWeather(location: Location, isRefresh: Boolean = false): Weather?
}