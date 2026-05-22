package com.pranshulgg.weather_master_app.data.local.dao.airquality

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pranshulgg.weather_master_app.data.local.entity.airquality.AirQualityWithRelations
import com.pranshulgg.weather_master_app.data.local.entity.airquality.CurrentAirQualityEntity

@Dao
interface AirQualityDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertCurrentAirQuality(currentAirQuality: CurrentAirQualityEntity)

    @Query("SELECT * FROM air_quality_current WHERE locationId = :locationId")
    suspend fun getAirQualityForLocation(locationId: String): AirQualityWithRelations?

    @Query("DELETE FROM weather_locations WHERE id = :id")
    suspend fun deleteCurrentAirQuality(id: String)
}