package com.pranshulgg.weather_master_app.data.local.dao.weather

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.pranshulgg.weather_master_app.data.local.entity.weather.CurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.entity.weather.DailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.entity.weather.HourlyWeatherEntity

@Dao
interface WeatherDao {

    @Transaction
    suspend fun insertWeather(
        currentWeather: CurrentWeatherEntity,
        hourlyWeather: List<HourlyWeatherEntity>,
        dailyWeather: List<DailyWeatherEntity>,
        id: String
    ) {
        deleteDailyDataForLocation(id)
        deleteHourlyDataForLocation(id)
        insertCurrentWeather(currentWeather)
        insertHourlyWeather(hourlyWeather)
        insertDailyWeather(dailyWeather)
    }

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertCurrentWeather(currentWeather: CurrentWeatherEntity)


    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertHourlyWeather(hourlyWeather: List<HourlyWeatherEntity>)


    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertDailyWeather(dailyWeather: List<DailyWeatherEntity>)

    @Query("DELETE FROM weather_daily WHERE locationId = :id")
    suspend fun deleteDailyDataForLocation(id: String)

    @Query("DELETE FROM weather_hourly WHERE locationId = :id")
    suspend fun deleteHourlyDataForLocation(id: String)
}