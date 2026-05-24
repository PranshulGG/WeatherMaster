package com.pranshulgg.weather_master_app.data.local.dao.weather

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pranshulgg.weather_master_app.core.model.weather.DistanceUnit
import com.pranshulgg.weather_master_app.core.model.weather.PrecipitationUnit
import com.pranshulgg.weather_master_app.core.model.weather.PressureUnit
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit
import com.pranshulgg.weather_master_app.data.local.entity.weather.units.AppWeatherUnitsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherUnitsDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(weatherUnits: AppWeatherUnitsEntity)

    @Query("SELECT * FROM weather_units")
    fun getWeatherUnits(): Flow<AppWeatherUnitsEntity?>

    @Query("SELECT * FROM weather_units")
    suspend fun getWeatherUnitsOnce(): AppWeatherUnitsEntity?

    @Query("SELECT * FROM weather_units LIMIT 1")
    suspend fun getOnce(): AppWeatherUnitsEntity?

    @Query("UPDATE weather_units SET tempUnit = :tempUnit WHERE id = 1")
    suspend fun updateTemperatureUnit(tempUnit: TemperatureUnit)


    @Query("UPDATE weather_units SET pressureUnit = :pressureUnit WHERE id = 1")
    suspend fun updatePressureUnit(pressureUnit: PressureUnit)

    @Query("UPDATE weather_units SET windUnit = :windSpeedUnit WHERE id = 1")
    suspend fun updateWindSpeedUnit(windSpeedUnit: WindSpeedUnit)

    @Query("UPDATE weather_units SET distanceUnit = :distanceUnit WHERE id = 1")
    suspend fun updateDistanceUnit(distanceUnit: DistanceUnit)

    @Query("UPDATE weather_units SET precipitationUnit = :precipitationUnit WHERE id = 1")
    suspend fun updatePrecipitationUnit(precipitationUnit: PrecipitationUnit)

}