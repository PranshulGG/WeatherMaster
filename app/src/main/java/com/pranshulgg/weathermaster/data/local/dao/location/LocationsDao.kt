package com.pranshulgg.weathermaster.data.local.dao.location

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.pranshulgg.weathermaster.core.model.sources.SearchSource
import com.pranshulgg.weathermaster.core.model.sources.WeatherSource
import com.pranshulgg.weathermaster.data.local.entity.location.WeatherLocationEntity
import com.pranshulgg.weathermaster.data.local.entity.weather.WeatherWithRelations
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationsDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertWeatherLocation(
        weatherLocation: WeatherLocationEntity
    )

    @Query("SELECT * FROM weather_locations ORDER BY isDefault DESC")
    fun getLocations(): Flow<List<WeatherLocationEntity>>

    @Query("DELETE FROM weather_locations WHERE id = :id")
    suspend fun deleteLocation(id: String)

    @Query("UPDATE weather_locations SET isDefault = 0")
    suspend fun clearDefaultLocations()

    @Query("UPDATE weather_locations SET isDefault = 1 WHERE id = :id")
    suspend fun updateDefaultLocation(id: String)

    @Query("SELECT COUNT(*) FROM weather_locations")
    suspend fun getLocationsCount(): Int

    @Query("SELECT * FROM weather_locations WHERE isDefault = 1 LIMIT 1")
    fun getDefaultLocation(): Flow<WeatherLocationEntity?>

    @Query("UPDATE weather_locations SET lat = :lat, lon = :lon WHERE isDeviceLocation = 1")
    suspend fun updateDeviceLocationPosition(lat: Double, lon: Double)

    @Transaction
    @Query("SELECT * FROM weather_locations WHERE id = :locationId LIMIT 1")
    suspend fun getWeatherForLocation(locationId: String): WeatherWithRelations

    @Transaction
    @Query("SELECT * FROM weather_locations WHERE id = :locationId")
    suspend fun getWeatherDataForLocation(locationId: String): WeatherWithRelations?

    @Transaction
    @Query("SELECT * FROM weather_locations")
    fun getAllLocationsCurrentWeather(): Flow<List<WeatherWithRelations>>

    @Query("UPDATE weather_locations SET source = :source WHERE id = :id")
    suspend fun updateSourceForLocation(id: String, source: WeatherSource)

    @Query("DELETE FROM weather_daily WHERE locationId = :id")
    suspend fun deleteDailyDataForLocation(id: String)

    @Query("DELETE FROM weather_hourly WHERE locationId = :id")
    suspend fun deleteHourlyDataForLocation(id: String)
}