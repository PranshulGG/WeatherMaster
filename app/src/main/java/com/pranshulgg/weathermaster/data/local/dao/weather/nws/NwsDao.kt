package com.pranshulgg.weathermaster.data.local.dao.weather.nws

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pranshulgg.weathermaster.data.local.entity.weather.nws.NwsGridPointsEntity
import com.pranshulgg.weathermaster.data.local.entity.weather.units.AppWeatherUnitsEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface NwsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocationGridPoints(grid: NwsGridPointsEntity)

    @Query("SELECT * FROM nws_location_gridpoints WHERE locationId = :locationId")
    suspend fun getGridPointsForLocation(locationId: String): NwsGridPointsEntity?

    @Query("DELETE FROM nws_location_gridpoints WHERE locationId = :locationId")
    suspend fun deleteGridPointsForLocation(locationId: String)
}
