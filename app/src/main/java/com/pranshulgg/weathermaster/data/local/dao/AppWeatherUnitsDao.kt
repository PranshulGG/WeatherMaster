package com.pranshulgg.weathermaster.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pranshulgg.weathermaster.core.model.DistanceUnits
import com.pranshulgg.weathermaster.core.model.PrecipitationUnits
import com.pranshulgg.weathermaster.core.model.PressureUnits
import com.pranshulgg.weathermaster.core.model.TemperatureUnits
import com.pranshulgg.weathermaster.core.model.WindSpeedUnits
import com.pranshulgg.weathermaster.data.local.entity.AppWeatherUnitsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppWeatherUnitsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherUnits: AppWeatherUnitsEntity)

    @Query("SELECT * FROM weather_units")
    fun getWeatherUnits(): Flow<AppWeatherUnitsEntity?>

    @Query("SELECT * FROM weather_units LIMIT 1")
    suspend fun getOnce(): AppWeatherUnitsEntity?

    @Query("UPDATE weather_units SET tempUnit = :tempUnit WHERE id = 1")
    suspend fun updateTemperatureUnit(tempUnit: TemperatureUnits)


    @Query("UPDATE weather_units SET pressureUnit = :pressureUnit WHERE id = 1")
    suspend fun updatePressureUnit(pressureUnit: PressureUnits)

    @Query("UPDATE weather_units SET windUnit = :windSpeedUnit WHERE id = 1")
    suspend fun updateWindSpeedUnit(windSpeedUnit: WindSpeedUnits)

    @Query("UPDATE weather_units SET distanceUnit = :distanceUnit WHERE id = 1")
    suspend fun updateDistanceUnit(distanceUnit: DistanceUnits)

    @Query("UPDATE weather_units SET precipitationUnit = :precipitationUnit WHERE id = 1")
    suspend fun updatePrecipitationUnit(precipitationUnit: PrecipitationUnits)

}