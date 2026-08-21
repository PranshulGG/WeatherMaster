package com.pranshulgg.weather_master_app.data.local.dao.location

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pranshulgg.weather_master_app.data.local.entity.location.LocationKeyEntity


@Dao
interface LocationKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCityKey(entity: LocationKeyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<LocationKeyEntity>)

    @Query("SELECT * FROM location_keys WHERE locationId = :locationId")
    suspend fun getCityKeyForLocation(locationId: String): LocationKeyEntity?

    @Query("SELECT * FROM location_keys")
    suspend fun getAllCityKeys(): List<LocationKeyEntity>

    @Query("DELETE FROM location_keys WHERE locationId = :locationId")
    suspend fun deleteCityKeyForLocation(locationId: String)

    @Query("DELETE FROM location_keys")
    suspend fun deleteAll()
}