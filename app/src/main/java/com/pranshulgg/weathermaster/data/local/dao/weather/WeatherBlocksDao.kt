package com.pranshulgg.weathermaster.data.local.dao.weather

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pranshulgg.weathermaster.data.local.entity.weather.blocks.WeatherBlockEntity

@Dao
interface WeatherBlocksDao {

    @Query("SELECT * FROM weather_blocks ORDER BY position ASC")
    suspend fun getBlocks(): List<WeatherBlockEntity>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertBlocks(blocks: List<WeatherBlockEntity>)

    @Query("DELETE FROM weather_blocks WHERE isDaily = 0")
    suspend fun clearMainBlocks()

    @Query("DELETE FROM weather_blocks WHERE isDaily = 1")
    suspend fun clearDailyBlocks()
}