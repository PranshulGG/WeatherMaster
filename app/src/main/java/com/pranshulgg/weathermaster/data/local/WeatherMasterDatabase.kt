package com.pranshulgg.weathermaster.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pranshulgg.weathermaster.data.local.dao.airquality.AirQualityDao
import com.pranshulgg.weathermaster.data.local.dao.weather.WeatherUnitsDao
import com.pranshulgg.weathermaster.data.local.dao.location.LocationsDao
import com.pranshulgg.weathermaster.data.local.dao.weather.WeatherBlocksDao
import com.pranshulgg.weathermaster.data.local.dao.weather.WeatherDao
import com.pranshulgg.weathermaster.data.local.dao.weather.nws.NwsDao
import com.pranshulgg.weathermaster.data.local.entity.weather.units.AppWeatherUnitsEntity
import com.pranshulgg.weathermaster.data.local.entity.airquality.CurrentAirQualityEntity
import com.pranshulgg.weathermaster.data.local.entity.weather.CurrentWeatherEntity
import com.pranshulgg.weathermaster.data.local.entity.weather.DailyWeatherEntity
import com.pranshulgg.weathermaster.data.local.entity.weather.HourlyWeatherEntity
import com.pranshulgg.weathermaster.data.local.entity.weather.blocks.WeatherBlockEntity
import com.pranshulgg.weathermaster.data.local.entity.location.WeatherLocationEntity
import com.pranshulgg.weathermaster.data.local.entity.weather.nws.NwsGridPointsEntity

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
    version = 38
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
                ).fallbackToDestructiveMigration(true).build().also { INSTANCE = it }
            }
    }

}