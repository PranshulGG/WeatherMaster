package com.pranshulgg.weather_master_app.data.local.dao.alerts

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.pranshulgg.weather_master_app.data.local.entity.airquality.CurrentAirQualityEntity
import com.pranshulgg.weather_master_app.data.local.entity.alerts.AlertEntity


@Dao
interface AlertsDao {

    @Transaction
    suspend fun insertAlerts(alerts: List<AlertEntity>, locationId: String) {
        deleteAlertsForLocation(locationId)
        insertAlertList(alerts)
    }


    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAlertList(alerts: List<AlertEntity>)

    @Query("SELECT * FROM alerts WHERE locationId = :locationId")
    suspend fun getAlertsForLocation(locationId: String): List<AlertEntity?>

    @Query("DELETE FROM alerts WHERE locationId = :locationId")
    suspend fun deleteAlertsForLocation(locationId: String)

}