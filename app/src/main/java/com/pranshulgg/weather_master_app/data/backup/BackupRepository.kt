package com.pranshulgg.weather_master_app.data.backup

import androidx.room.withTransaction
import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlockType
import com.pranshulgg.weather_master_app.core.prefs.helper.PreferencesHelper
import com.pranshulgg.weather_master_app.data.backup.model.AppPrefsBackup
import com.pranshulgg.weather_master_app.data.backup.model.BACKUP_SCHEMA_VERSION
import com.pranshulgg.weather_master_app.data.backup.model.BackupPayload
import com.pranshulgg.weather_master_app.data.backup.model.WeatherBlockBackup
import com.pranshulgg.weather_master_app.data.backup.model.toBackupDto
import com.pranshulgg.weather_master_app.data.backup.model.toEntity
import com.pranshulgg.weather_master_app.data.local.WeatherMasterDatabase
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.ApiKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherBlocksDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherUnitsDao
import com.pranshulgg.weather_master_app.data.local.entity.weather.blocks.WeatherBlockEntity
import kotlinx.serialization.json.Json
import javax.inject.Inject

class BackupRepository @Inject constructor(
    private val db: WeatherMasterDatabase,
    private val locationsDao: LocationsDao,
    private val locationKeysDao: LocationKeysDao,
    private val apiKeysDao: ApiKeysDao,
    private val weatherUnitsDao: WeatherUnitsDao,
    private val weatherBlocksDao: WeatherBlocksDao
) {

    suspend fun buildBackup(includeApiKeys: Boolean): BackupPayload {
        val locations = locationsDao.getLocationsOnce()
        val locationKeys = locationKeysDao.getAllCityKeys()
        val apiKeys = if (includeApiKeys) apiKeysDao.getAllApiKeys() else emptyList()
        val weatherUnits = weatherUnitsDao.getOnce()
        val weatherBlocks = weatherBlocksDao.getBlocks()

        return BackupPayload(
            exportedAt = System.currentTimeMillis(),
            locations = locations.map { it.toBackupDto() },
            locationKeys = locationKeys.map { it.toBackupDto() },
            apiKeys = apiKeys.map { it.toBackupDto() },
            weatherUnits = weatherUnits?.toBackupDto(),
            weatherBlocks = weatherBlocks.map { it.toBackupDto() },
            prefs = readPrefsBackup()
        )
    }

    suspend fun serializeBackup(includeApiKeys: Boolean): String {
        val payload = buildBackup(includeApiKeys)
        return Json { prettyPrint = true }.encodeToString(BackupPayload.serializer(), payload)
    }

    suspend fun restoreBackup(payload: BackupPayload) {
        if (payload.schemaVersion > BACKUP_SCHEMA_VERSION) {
            throw AppException.BackupSchemaVersionUnsupported()
        }

        // A location list with no default would leave the app in a state it never expects
        // (default-location lookups assume exactly one exists) - reject the whole import
        // rather than silently producing that state.
        if (payload.locations.none { it.isDefault }) {
            throw AppException.BackupMissingDefaultLocation()
        }

        db.withTransaction {
            locationKeysDao.deleteAll()
            locationsDao.deleteAllLocations()
            locationsDao.insertAllLocations(payload.locations.map { it.toEntity() })
            locationKeysDao.insertAll(payload.locationKeys.map { it.toEntity() })

            // An empty list means the export toggle was off (or there was nothing to export),
            // not "the user wants their keys deleted" - so existing keys are left untouched
            // instead of being wiped, unlike everything else in a full-replace restore.
            if (payload.apiKeys.isNotEmpty()) {
                apiKeysDao.deleteAllApiKeys()
                apiKeysDao.insertAll(payload.apiKeys.map { it.toEntity() })
            }

            payload.weatherUnits?.let { weatherUnitsDao.insert(it.toEntity()) }

            weatherBlocksDao.clearMainBlocks()
            weatherBlocksDao.clearDailyBlocks()
            weatherBlocksDao.insertBlocks(
                payload.weatherBlocks.mapNotNull { it.toEntityOrNull() }
            )
        }

        payload.prefs.applyToPreferences()
    }

    private fun readPrefsBackup(): AppPrefsBackup = AppPrefsBackup(
        appTheme = PreferencesHelper.getString("app_theme"),
        customThemeColor = PreferencesHelper.getString("custom_theme_color"),
        isCustomTheme = PreferencesHelper.getBool("isCustomTheme"),
        isDynamicTheme = PreferencesHelper.getBool("isDynamicTheme"),
        themeVariantType = PreferencesHelper.getString("theme_variant_type"),
        searchSource = PreferencesHelper.getString("searchSource"),
        backgroundUpdatesEnabled = PreferencesHelper.getBool("backgroundUpdatesEnabled"),
        backgroundUpdatesInterval = PreferencesHelper.getInt("backgroundUpdatesInterval"),
        isFroggyLayout = PreferencesHelper.getBool("isFroggyLayout"),
        isShowWeatherAnimations = PreferencesHelper.getBool("isShowWeatherAnimations"),
        isWeatherBasedTheme = PreferencesHelper.getBool("isWeatherBasedTheme"),
        is24HrTimeFormat = PreferencesHelper.getBool("is24HrTimeFormat"),
        isShowSummary = PreferencesHelper.getBool("isShowSummary"),
        isGoogleSansFlex = PreferencesHelper.getBool("isGoogleSansFlex")
    )

    private fun AppPrefsBackup.applyToPreferences() {
        appTheme?.let { PreferencesHelper.setString("app_theme", it) }
        customThemeColor?.let { PreferencesHelper.setString("custom_theme_color", it) }
        isCustomTheme?.let { PreferencesHelper.setBool("isCustomTheme", it) }
        isDynamicTheme?.let { PreferencesHelper.setBool("isDynamicTheme", it) }
        themeVariantType?.let { PreferencesHelper.setString("theme_variant_type", it) }
        searchSource?.let { PreferencesHelper.setString("searchSource", it) }
        backgroundUpdatesEnabled?.let { PreferencesHelper.setBool("backgroundUpdatesEnabled", it) }
        backgroundUpdatesInterval?.let { PreferencesHelper.setInt("backgroundUpdatesInterval", it) }
        isFroggyLayout?.let { PreferencesHelper.setBool("isFroggyLayout", it) }
        isShowWeatherAnimations?.let { PreferencesHelper.setBool("isShowWeatherAnimations", it) }
        isWeatherBasedTheme?.let { PreferencesHelper.setBool("isWeatherBasedTheme", it) }
        is24HrTimeFormat?.let { PreferencesHelper.setBool("is24HrTimeFormat", it) }
        isShowSummary?.let { PreferencesHelper.setBool("isShowSummary", it) }
        isGoogleSansFlex?.let { PreferencesHelper.setBool("isGoogleSansFlex", it) }
    }

    private fun WeatherBlockBackup.toEntityOrNull(): WeatherBlockEntity? {
        val blockType = runCatching { WeatherBlockType.valueOf(type) }.getOrNull() ?: return null
        return WeatherBlockEntity(
            isDaily = isDaily,
            type = blockType,
            isHidden = isHidden,
            position = position
        )
    }
}
