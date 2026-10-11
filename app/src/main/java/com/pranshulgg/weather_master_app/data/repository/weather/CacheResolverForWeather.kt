package com.pranshulgg.weather_master_app.data.repository.weather

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherHourly
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResultType
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnWeatherCache
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.mergeHourlyWeather
import com.pranshulgg.weather_master_app.data.local.dao.weather.ApiKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.entity.weather.ApiKeyEntity
import com.pranshulgg.weather_master_app.data.local.entity.weather.HourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toHourlyWeatherEntity
import javax.inject.Inject

data class CacheModel(
    val cachedWeather: Weather?,
    val cachedHourly: List<HourlyWeatherEntity>,
    val type: CacheModelResultType,
    val apiKey: String? = null
)


enum class CacheModelResultType {
    REFRESH_TOO_EARLY,
    RETURN_CACHE,
    FETCH,
    NO_API_KEY_ERROR
}

class CacheResolverForWeather @Inject constructor(
    val dao: WeatherContextDao,
    val weatherDao: WeatherDao,
    val apiKeysDao: ApiKeysDao
) {

    suspend fun resolve(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean
    ): CacheModel {

        val cache = dao.getWeatherDataForLocation(location.id)

        val shouldReturnCache = shouldReturnWeatherCache(cache, isManualRefresh, isForceRefresh)
        val existingHourly = weatherDao.getHourlyDataForLocation(location.id, location.source)

        val locationRequiresApiKey = location.source.requiresUserApiKey

        val apiKey = if (locationRequiresApiKey)
            apiKeysDao.getApiKeyForSource(location.source) else null

        if (locationRequiresApiKey && apiKey?.apiKey.isNullOrBlank()) {
            CacheModel(
                cachedWeather = cache?.toDomain(),
                cachedHourly = existingHourly,
                type = CacheModelResultType.NO_API_KEY_ERROR,
                apiKey = apiKey?.apiKey
            )
        }

        when (shouldReturnCache) {
            WeatherResultType.REFRESH_TOO_EARLY -> return CacheModel(
                cachedWeather = cache?.toDomain(),
                cachedHourly = existingHourly,
                type = CacheModelResultType.REFRESH_TOO_EARLY,
                apiKey = apiKey?.apiKey
            )


            WeatherResultType.SUCCESS -> return CacheModel(
                cachedWeather = cache?.toDomain(),
                cachedHourly = existingHourly,
                type = CacheModelResultType.RETURN_CACHE,
                apiKey = apiKey?.apiKey
            )

            else -> {
                return CacheModel(
                    cachedWeather = cache?.toDomain(),
                    cachedHourly = existingHourly,
                    type = CacheModelResultType.FETCH,
                    apiKey = apiKey?.apiKey
                )
            }
        }
    }


}
