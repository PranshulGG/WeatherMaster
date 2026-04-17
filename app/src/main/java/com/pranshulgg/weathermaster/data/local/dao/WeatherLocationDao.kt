package com.pranshulgg.weathermaster.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.pranshulgg.weathermaster.data.local.entity.WeatherLocationEntity

@Dao
interface WeatherLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherLocation(weatherLocation: WeatherLocationEntity)

}