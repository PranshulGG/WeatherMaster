package com.pranshulgg.weather_master_app.data.local

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pranshulgg.weather_master_app.data.local.dao.airquality.AirQualityDao
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.github.GithubDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.ApiKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherBlocksDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherUnitsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.nws.NwsDao
import com.pranshulgg.weather_master_app.data.local.entity.airquality.CurrentAirQualityEntity
import com.pranshulgg.weather_master_app.data.local.entity.airquality.HourlyAirQualityEntity
import com.pranshulgg.weather_master_app.data.local.entity.alerts.AlertEntity
import com.pranshulgg.weather_master_app.data.local.entity.github.GithubEntity
import com.pranshulgg.weather_master_app.data.local.entity.location.LocationKeyEntity
import com.pranshulgg.weather_master_app.data.local.entity.location.WeatherLocationEntity
import com.pranshulgg.weather_master_app.data.local.entity.weather.ApiKeyEntity
import com.pranshulgg.weather_master_app.data.local.entity.weather.CurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.entity.weather.DailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.entity.weather.HourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.entity.weather.blocks.WeatherBlockEntity
import com.pranshulgg.weather_master_app.data.local.entity.weather.nws.NwsGridPointsEntity
import com.pranshulgg.weather_master_app.data.local.entity.weather.units.AppWeatherUnitsEntity

@Database(
    entities = [
        WeatherLocationEntity::class,
        CurrentWeatherEntity::class,
        HourlyWeatherEntity::class,
        DailyWeatherEntity::class,
        AppWeatherUnitsEntity::class,
        WeatherBlockEntity::class,
        CurrentAirQualityEntity::class,
        NwsGridPointsEntity::class,
        GithubEntity::class,
        HourlyAirQualityEntity::class,
        LocationKeyEntity::class,
        AlertEntity::class,
        ApiKeyEntity::class
    ],
    version = 58,
    autoMigrations = [
        AutoMigration(from = 39, to = 40),
        AutoMigration(from = 42, to = 43),
        AutoMigration(from = 43, to = 44),
        AutoMigration(from = 44, to = 45),
        AutoMigration(from = 47, to = 48),
        AutoMigration(from = 48, to = 49),
        AutoMigration(from = 49, to = 50),
        AutoMigration(from = 50, to = 51),
        AutoMigration(from = 52, to = 53),
        AutoMigration(from = 53, to = 54),
        AutoMigration(from = 54, to = 55),
        AutoMigration(from = 56, to = 57),
        AutoMigration(from = 57, to = 58)
    ]
)
abstract class WeatherMasterDatabase : RoomDatabase() {

    abstract fun locationsDao(): LocationsDao
    abstract fun weatherDao(): WeatherDao
    abstract fun weatherUnitsDao(): WeatherUnitsDao
    abstract fun weatherBlocksDao(): WeatherBlocksDao
    abstract fun airQualityDao(): AirQualityDao
    abstract fun nwsDao(): NwsDao
    abstract fun githubDao(): GithubDao

    abstract fun locationKeysDao(): LocationKeysDao

    abstract fun alertsDao(): AlertsDao

    abstract fun apiKeysDao(): ApiKeysDao

    companion object {

        @Volatile
        private var INSTANCE: WeatherMasterDatabase? = null

        fun getInstance(context: Context): WeatherMasterDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    WeatherMasterDatabase::class.java,
                    "weather_master.db"
                ).addMigrations(
                    MIGRATION_40_41,
                    MIGRATION_41_42,
                    MIGRATION_45_46,
                    MIGRATION_46_47,
                    MIGRATION_51_52,
                    MIGRATION_55_56
                )
                    .build()
                    .also { INSTANCE = it }
            }
    }
}


val MIGRATION_40_41 = object : Migration(40, 41) {
    override fun migrate(db: SupportSQLiteDatabase) {

        /**
         * Need to check if the column already exists
         * because I forgot when it was added? and it keeps crashing because it already exists
         */
        val all = db.query("PRAGMA table_info(weather_hourly)")
        var exists = false

        while (all.moveToNext()) {
            val columnName = all.getString(
                all.getColumnIndexOrThrow("name")
            )

            if (columnName == "dewPoint") {
                exists = true
                break
            }
        }
        all.close()
        if (!exists) {
            db.execSQL(
                "ALTER TABLE weather_hourly ADD COLUMN dewPoint REAL"
            )
        }
    }
}

val MIGRATION_41_42 = object : Migration(41, 42) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            ALTER TABLE weather_daily ADD COLUMN dusk INTEGER NOT NULL Default 0
        """.trimIndent()
        )
        db.execSQL(
            """
            ALTER TABLE weather_daily ADD COLUMN dawn INTEGER NOT NULL Default 0
        """.trimIndent()
        )
    }
}


val MIGRATION_45_46 = object : Migration(45, 46) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS air_quality_hourly")

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS air_quality_hourly (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                locationId TEXT NOT NULL,
                time INTEGER NOT NULL,
                carbonMonoxide REAL,
                nitrogenDioxide REAL,
                ozone REAL,
                pm10 REAL,
                pm25 REAL,
                sulphurDioxide REAL,
                FOREIGN KEY(locationId) REFERENCES weather_locations(id) ON DELETE CASCADE
            )
        """.trimIndent()
        )

        db.execSQL(
            "CREATE INDEX IF NOT EXISTS index_air_quality_hourly_locationId ON air_quality_hourly(locationId)"
        )
    }
}

val MIGRATION_46_47 = object : Migration(46, 47) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE weather_locations ADD COLUMN alertSource TEXT NOT NULL DEFAULT 'NONE'")
        db.execSQL("ALTER TABLE weather_locations ADD COLUMN airQualitySource TEXT NOT NULL DEFAULT 'OPEN_METEO'")
    }
}

val MIGRATION_51_52 = object : Migration(51, 52) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE accu_locations RENAME TO location_keys")
    }
}


val MIGRATION_55_56 = object : Migration(55, 56) {
    override fun migrate(db: SupportSQLiteDatabase) {
        val cursor = db.query("PRAGMA table_info(weather_hourly)")
        var cachedSourceExists = false

        while (cursor.moveToNext()) {
            val columnName = cursor.getString(
                cursor.getColumnIndexOrThrow("name")
            )

            if (columnName == "cachedSource") {
                cachedSourceExists = true
                break
            }
        }

        cursor.close()

        if (!cachedSourceExists) {
            db.execSQL(
                "ALTER TABLE weather_hourly ADD COLUMN cachedSource TEXT"
            )
        }
    }
}