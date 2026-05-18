package com.pranshulgg.weathermaster.data.local.dao.weather

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pranshulgg.weathermaster.core.model.weather.DistanceUnits
import com.pranshulgg.weathermaster.core.model.weather.PrecipitationUnits
import com.pranshulgg.weathermaster.core.model.weather.PressureUnits
import com.pranshulgg.weathermaster.core.model.weather.TemperatureUnits
import com.pranshulgg.weathermaster.core.model.weather.WindSpeedUnits
import com.pranshulgg.weathermaster.data.local.entity.weather.units.AppWeatherUnitsEntity
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