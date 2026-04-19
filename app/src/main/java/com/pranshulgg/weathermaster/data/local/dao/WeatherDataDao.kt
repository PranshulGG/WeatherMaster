package com.pranshulgg.weathermaster.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.pranshulgg.weathermaster.data.local.entity.CurrentWeatherEntity
import com.pranshulgg.weathermaster.data.local.entity.DailyWeatherEntity
import com.pranshulgg.weathermaster.data.local.entity.HourlyWeatherEntity
import com.pranshulgg.weathermaster.data.local.entity.WeatherWithRelations

@Dao
interface WeatherDataDao {

    @Transaction
    suspend fun insertWeather(
        currentWeather: CurrentWeatherEntity,
        hourlyWeather: List<HourlyWeatherEntity>,
        dailyWeather: List<DailyWeatherEntity>
    ) {
        insertCurrentWeather(currentWeather)
        insertHourlyWeather(hourlyWeather)
        insertDailyWeather(dailyWeather)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(currentWeather: CurrentWeatherEntity)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourlyWeather(hourlyWeather: List<HourlyWeatherEntity>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyWeather(dailyWeather: List<DailyWeatherEntity>)

    @Query("SELECT * FROM weather_locations WHERE id = :locationId")
    suspend fun getAllWeatherDataForLocation(locationId: String): WeatherWithRelations?


}