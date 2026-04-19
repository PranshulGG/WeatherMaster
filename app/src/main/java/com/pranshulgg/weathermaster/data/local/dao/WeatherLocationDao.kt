package com.pranshulgg.weathermaster.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pranshulgg.weathermaster.core.model.Location
import com.pranshulgg.weathermaster.data.local.entity.WeatherLocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherLocation(
        weatherLocation: WeatherLocationEntity
    )

    @Query("SELECT * FROM weather_locations")
    fun getLocations(): Flow<List<WeatherLocationEntity>>

    @Query("DELETE FROM weather_locations WHERE id = :id")
    suspend fun deleteLocation(id: String)

    @Query("UPDATE weather_locations SET isDefault = 1 WHERE id = :id")
    suspend fun updateDefaultLocation(id: String)

    @Query("SELECT COUNT(*) FROM weather_locations")
    suspend fun getLocationsCount(): Int

    @Query("SELECT * FROM weather_locations WHERE isDefault = 1 LIMIT 1")
    fun getDefaultLocation(): Flow<WeatherLocationEntity?>
}