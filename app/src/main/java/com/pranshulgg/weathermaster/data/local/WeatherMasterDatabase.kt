package com.pranshulgg.weathermaster.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pranshulgg.weathermaster.data.local.dao.AirQualityDao
import com.pranshulgg.weathermaster.data.local.dao.AppWeatherUnitsDao
import com.pranshulgg.weathermaster.data.local.dao.WeatherBlocksDao
import com.pranshulgg.weathermaster.data.local.dao.WeatherDataDao
import com.pranshulgg.weathermaster.data.local.dao.WeatherLocationDao
import com.pranshulgg.weathermaster.data.local.entity.AppWeatherUnitsEntity
import com.pranshulgg.weathermaster.data.local.entity.CurrentAirQualityEntity
import com.pranshulgg.weathermaster.data.local.entity.CurrentWeatherEntity
import com.pranshulgg.weathermaster.data.local.entity.DailyWeatherEntity
import com.pranshulgg.weathermaster.data.local.entity.HourlyWeatherEntity
import com.pranshulgg.weathermaster.data.local.entity.WeatherBlockEntity
import com.pranshulgg.weathermaster.data.local.entity.WeatherLocationEntity

@Database(
    entities = [WeatherLocationEntity::class, CurrentWeatherEntity::class, HourlyWeatherEntity::class, DailyWeatherEntity::class, AppWeatherUnitsEntity::class, WeatherBlockEntity::class, CurrentAirQualityEntity::class],
    version = 31
)
abstract class WeatherMasterDatabase : RoomDatabase() {

    abstract fun weatherLocationDao(): WeatherLocationDao
    abstract fun weatherDataDao(): WeatherDataDao

    abstract fun appWeatherUnitsDao(): AppWeatherUnitsDao

    abstract fun weatherBlocksDao(): WeatherBlocksDao

    abstract fun airQualityDao(): AirQualityDao

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