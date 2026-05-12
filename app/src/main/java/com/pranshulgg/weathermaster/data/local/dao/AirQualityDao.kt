package com.pranshulgg.weathermaster.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pranshulgg.weathermaster.data.local.entity.AirQualityWithRelations
import com.pranshulgg.weathermaster.data.local.entity.CurrentAirQualityEntity

@Dao
interface AirQualityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentAirQuality(currentAirQuality: CurrentAirQualityEntity)

    @Query("SELECT * FROM air_quality_current WHERE locationId = :locationId")
    suspend fun getAirQualityForLocation(locationId: String): AirQualityWithRelations?
}