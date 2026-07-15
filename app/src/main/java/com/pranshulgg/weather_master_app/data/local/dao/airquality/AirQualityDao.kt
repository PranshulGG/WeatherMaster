package com.pranshulgg.weather_master_app.data.local.dao.airquality

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.pranshulgg.weather_master_app.data.local.entity.airquality.AirQualityWithRelations
import com.pranshulgg.weather_master_app.data.local.entity.airquality.CurrentAirQualityEntity
import com.pranshulgg.weather_master_app.data.local.entity.airquality.HourlyAirQualityEntity

@Dao
interface AirQualityDao {

    @Transaction
    suspend fun insertAirQuality(
        currentAirQuality: CurrentAirQualityEntity,
        hourlyAirQuality: List<HourlyAirQualityEntity>
    ) {
        insertCurrentAirQuality(currentAirQuality)
        insertHourlyAirQuality(hourlyAirQuality)
    }


    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertCurrentAirQuality(currentAirQuality: CurrentAirQualityEntity)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertHourlyAirQuality(hourlyAirQuality: List<HourlyAirQualityEntity>)

    @Query("SELECT * FROM air_quality_current WHERE locationId = :locationId")
    suspend fun getAirQualityForLocation(locationId: String): AirQualityWithRelations?

    @Query("DELETE FROM weather_locations WHERE id = :id")
    suspend fun deleteCurrentAirQuality(id: String)
}