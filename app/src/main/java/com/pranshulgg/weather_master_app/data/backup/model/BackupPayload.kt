package com.pranshulgg.weather_master_app.data.backup.model

import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.DistanceUnit
import com.pranshulgg.weather_master_app.core.model.weather.PrecipitationUnit
import com.pranshulgg.weather_master_app.core.model.weather.PressureUnit
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit
import com.pranshulgg.weather_master_app.core.model.weather.openmeteo.OpenMeteoModel
import com.pranshulgg.weather_master_app.data.local.entity.location.LocationKeyEntity
import com.pranshulgg.weather_master_app.data.local.entity.location.WeatherLocationEntity
import com.pranshulgg.weather_master_app.data.local.entity.weather.ApiKeyEntity
import com.pranshulgg.weather_master_app.data.local.entity.weather.blocks.WeatherBlockEntity
import com.pranshulgg.weather_master_app.data.local.entity.weather.units.AppWeatherUnitsEntity
import kotlinx.serialization.Serializable

const val BACKUP_SCHEMA_VERSION = 1

// Dedicated DTOs rather than the Room @Entity classes directly - the entities' shape is
// governed by WeatherMasterDatabase's migration history, which shouldn't also have to stay
// backward-compatible with old export files.
@Serializable
data class BackupPayload(
    val schemaVersion: Int = BACKUP_SCHEMA_VERSION,
    val exportedAt: Long,
    val locations: List<LocationBackup>,
    val locationKeys: List<LocationKeyBackup>,
    val apiKeys: List<ApiKeyBackup> = emptyList(), // empty = opt-out was chosen, or none existed
    val weatherUnits: WeatherUnitsBackup?,
    val weatherBlocks: List<WeatherBlockBackup>,
    val prefs: AppPrefsBackup
)

@Serializable
data class LocationBackup(
    val id: String,
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val source: Source,
    val state: String? = null,
    val isFavorite: Boolean = false,
    val isPinned: Boolean = false,
    val countryCode: String? = null,
    val isDefault: Boolean = false,
    val isDeviceLocation: Boolean = false,
    val alertSource: Source,
    val airQualitySource: Source,
    val customName: String? = null,
    val openMeteoModel: OpenMeteoModel = OpenMeteoModel.BEST_MATCH,
    val alertsLastFetchedAt: Long? = null
)

@Serializable
data class LocationKeyBackup(
    val locationId: String,
    val cityKey: String
)

@Serializable
data class ApiKeyBackup(
    val source: Source,
    val apiKey: String?
)

@Serializable
data class WeatherUnitsBackup(
    val tempUnit: TemperatureUnit,
    val windUnit: WindSpeedUnit,
    val distanceUnit: DistanceUnit,
    val pressureUnit: PressureUnit,
    val precipitationUnit: PrecipitationUnit
)

// type is stored as a plain string (WeatherBlockType.name), not the enum directly -
// kotlinx.serialization fails the whole decode on an unrecognized enum constant, and a string
// lets a future block type addition degrade to "skip this one row" instead.
@Serializable
data class WeatherBlockBackup(
    val isDaily: Boolean,
    val type: String,
    val isHidden: Boolean,
    val position: Int
)

// Every field nullable: null means "leave this pref alone" on import, not "clear it" - so an
// older/partial export file still imports cleanly.
@Serializable
data class AppPrefsBackup(
    val appTheme: String? = null,
    val customThemeColor: String? = null,
    val isCustomTheme: Boolean? = null,
    val isDynamicTheme: Boolean? = null,
    val themeVariantType: String? = null,
    val searchSource: String? = null,
    val backgroundUpdatesEnabled: Boolean? = null,
    val backgroundUpdatesInterval: Int? = null,
    val isFroggyLayout: Boolean? = null,
    val isShowWeatherAnimations: Boolean? = null,
    val isWeatherBasedTheme: Boolean? = null,
    val is24HrTimeFormat: Boolean? = null,
    val isShowSummary: Boolean? = null,
    val isGoogleSansFlex: Boolean? = null
)

fun WeatherLocationEntity.toBackupDto(): LocationBackup = LocationBackup(
    id = id,
    name = name,
    country = country,
    lat = lat,
    lon = lon,
    timezone = timezone,
    source = source,
    state = state,
    isFavorite = isFavorite,
    isPinned = isPinned,
    countryCode = countryCode,
    isDefault = isDefault,
    isDeviceLocation = isDeviceLocation,
    alertSource = alertSource,
    airQualitySource = airQualitySource,
    customName = customName,
    openMeteoModel = openMeteoModel,
    alertsLastFetchedAt = alertsLastFetchedAt
)

fun LocationBackup.toEntity(): WeatherLocationEntity = WeatherLocationEntity(
    id = id,
    name = name,
    country = country,
    lat = lat,
    lon = lon,
    timezone = timezone,
    source = source,
    state = state,
    isFavorite = isFavorite,
    isPinned = isPinned,
    countryCode = countryCode,
    isDefault = isDefault,
    isDeviceLocation = isDeviceLocation,
    alertSource = alertSource,
    airQualitySource = airQualitySource,
    customName = customName,
    openMeteoModel = openMeteoModel,
    alertsLastFetchedAt = alertsLastFetchedAt
)

fun LocationKeyEntity.toBackupDto(): LocationKeyBackup = LocationKeyBackup(
    locationId = locationId,
    cityKey = cityKey
)

fun LocationKeyBackup.toEntity(): LocationKeyEntity = LocationKeyEntity(
    locationId = locationId,
    cityKey = cityKey
)

fun ApiKeyEntity.toBackupDto(): ApiKeyBackup = ApiKeyBackup(
    source = source,
    apiKey = apiKey
)

fun ApiKeyBackup.toEntity(): ApiKeyEntity = ApiKeyEntity(
    source = source,
    apiKey = apiKey
)

fun AppWeatherUnitsEntity.toBackupDto(): WeatherUnitsBackup = WeatherUnitsBackup(
    tempUnit = tempUnit,
    windUnit = windUnit,
    distanceUnit = distanceUnit,
    pressureUnit = pressureUnit,
    precipitationUnit = precipitationUnit
)

fun WeatherUnitsBackup.toEntity(): AppWeatherUnitsEntity = AppWeatherUnitsEntity(
    tempUnit = tempUnit,
    windUnit = windUnit,
    distanceUnit = distanceUnit,
    pressureUnit = pressureUnit,
    precipitationUnit = precipitationUnit
)

fun WeatherBlockEntity.toBackupDto(): WeatherBlockBackup = WeatherBlockBackup(
    isDaily = isDaily,
    type = type.name,
    isHidden = isHidden,
    position = position
)
