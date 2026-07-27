package com.pranshulgg.weather_master_app.data.local.dao.airquality.accu

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pranshulgg.weather_master_app.data.local.entity.airquality.accu.AccuEntity
import com.pranshulgg.weather_master_app.data.local.entity.weather.nws.NwsGridPointsEntity


@Dao
interface AccuDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCityKey(entity: AccuEntity)

    @Query("SELECT * FROM accu_locations WHERE locationId = :locationId")
    suspend fun getCityKeyForLocation(locationId: String): AccuEntity?

    @Query("DELETE FROM accu_locations WHERE locationId = :locationId")
    suspend fun deleteCityKeyForLocation(locationId: String)
}