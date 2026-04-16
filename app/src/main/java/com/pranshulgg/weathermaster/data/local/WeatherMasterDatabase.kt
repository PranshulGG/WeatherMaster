package com.pranshulgg.weathermaster.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pranshulgg.weathermaster.data.local.dao.WeatherDataDao

abstract class WeatherMasterDatabase : RoomDatabase() {

    abstract fun weatherDataDao(): WeatherDataDao

    companion object {

        @Volatile
        private var INSTANCE: WeatherMasterDatabase? = null

        fun getInstance(context: Context): WeatherMasterDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    WeatherMasterDatabase::class.java,
                    "weather_master.db"
                ).fallbackToDestructiveMigration(true).build().also { INSTANCE = it }
            }
    }

}