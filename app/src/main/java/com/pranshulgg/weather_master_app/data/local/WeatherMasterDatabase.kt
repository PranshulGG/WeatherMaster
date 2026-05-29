package com.pranshulgg.weather_master_app.data.local

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pranshulgg.weather_master_app.data.local.dao.airquality.AirQualityDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherUnitsDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherBlocksDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.nws.NwsDao
import com.pranshulgg.weather_master_app.data.local.entity.weather.units.AppWeatherUnitsEntity
import com.pranshulgg.weather_master_app.data.local.entity.airquality.CurrentAirQualityEntity
import com.pranshulgg.weather_master_app.data.local.entity.weather.CurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.entity.weather.DailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.entity.weather.HourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.entity.weather.blocks.WeatherBlockEntity
import com.pranshulgg.weather_master_app.data.local.entity.location.WeatherLocationEntity
import com.pranshulgg.weather_master_app.data.local.entity.weather.nws.NwsGridPointsEntity

@Database(
    entities = [
        WeatherLocationEntity::class,
        CurrentWeatherEntity::class,
        HourlyWeatherEntity::class,
        DailyWeatherEntity::class,
        AppWeatherUnitsEntity::class,
        WeatherBlockEntity::class,
        CurrentAirQualityEntity::class,
        NwsGridPointsEntity::class
    ],
    version = 40,
    autoMigrations = [
        AutoMigration(from = 39, to = 40)
    ]
)
abstract class WeatherMasterDatabase : RoomDatabase() {

    abstract fun locationsDao(): LocationsDao
    abstract fun weatherDao(): WeatherDao

    abstract fun weatherUnitsDao(): WeatherUnitsDao

    abstract fun weatherBlocksDao(): WeatherBlocksDao

    abstract fun airQualityDao(): AirQualityDao

    abstract fun nwsDao(): NwsDao

    companion object {

        @Volatile
        private var INSTANCE: WeatherMasterDatabase? = null

        fun getInstance(context: Context): WeatherMasterDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    WeatherMasterDatabase::class.java,
                    "weather_master.db"
                ).addMigrations().build().also { INSTANCE = it }
            }
    }
}

