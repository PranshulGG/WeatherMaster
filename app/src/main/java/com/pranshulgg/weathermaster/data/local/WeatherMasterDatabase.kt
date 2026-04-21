package com.pranshulgg.weathermaster.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pranshulgg.weathermaster.data.local.dao.AppWeatherUnitsDao
import com.pranshulgg.weathermaster.data.local.dao.WeatherDataDao
import com.pranshulgg.weathermaster.data.local.dao.WeatherLocationDao
import com.pranshulgg.weathermaster.data.local.entity.CurrentWeatherEntity
import com.pranshulgg.weathermaster.data.local.entity.WeatherLocationEntity
import com.pranshulgg.weathermaster.data.local.entity.HourlyWeatherEntity
import com.pranshulgg.weathermaster.data.local.entity.DailyWeatherEntity
import com.pranshulgg.weathermaster.data.local.entity.AppWeatherUnitsEntity

@Database(
    entities = [WeatherLocationEntity::class, CurrentWeatherEntity::class, HourlyWeatherEntity::class, DailyWeatherEntity::class, AppWeatherUnitsEntity::class],
    version = 15
)
abstract class WeatherMasterDatabase : RoomDatabase() {

    abstract fun weatherLocationDao(): WeatherLocationDao
    abstract fun weatherDataDao(): WeatherDataDao

    abstract fun appWeatherUnitsDao(): AppWeatherUnitsDao

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