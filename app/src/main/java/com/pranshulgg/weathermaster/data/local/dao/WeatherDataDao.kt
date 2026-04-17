package com.pranshulgg.weathermaster.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.pranshulgg.weathermaster.data.local.entity.CurrentWeatherEntity
import com.pranshulgg.weathermaster.data.local.entity.DailyWeatherEntity
import com.pranshulgg.weathermaster.data.local.entity.HourlyWeatherEntity

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


}