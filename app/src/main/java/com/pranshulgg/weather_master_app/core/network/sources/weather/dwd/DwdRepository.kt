package com.pranshulgg.weather_master_app.core.network.sources.weather.dwd

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.FinishedWeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResultType
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.network.sources.weather.dwd.json.bundle.DwdWeatherJsonBundle
import com.pranshulgg.weather_master_app.core.utils.formatters.safeZoneId
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnWeatherCache
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.mergeHourlyWeather
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.dwd.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.repository.weather.BaseWeatherRepository
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import com.pranshulgg.weather_master_app.data.repository.weather.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

class DwdRepository @Inject constructor(
    val dao: WeatherContextDao,
    val weatherDao: WeatherDao,
    val api: DwdApi
) : BaseWeatherRepository() {

    override val weatherSource = Source.DWD

    override suspend fun fetchAndProcessWeather(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean,
        cacheModel: CacheModel
    ): Weather {
        val response = safeApiCall {
            api.fetchCurrentWeather(
                location.latitude,
                location.longitude
            )
        }.getOrThrow()
        val dates = getStartEndDate(location)

        val forecastResponse = safeApiCall {
            api.fetchWeatherForecast(
                location.latitude, location.longitude, dates.first, dates.second
            )
        }.getOrThrow()


        val final = DwdWeatherJsonBundle(
            current = response,
            forecastJson = forecastResponse
        )

        val domain = final.toDomain(location)

        return domain
    }

    override suspend fun saveWeatherToDb(data: Weather, cacheModel: CacheModel) {
        useGenericSaveImplementation(cacheModel.cachedHourly, data, weatherDao)
    }

    override fun finishedWeatherResult(data: Weather): FinishedWeatherResult {
        return FinishedWeatherResult(weather = data)
    }

}

private fun getStartEndDate(location: Location): Pair<String, String> {
    val zoneId = safeZoneId(location.timezone)

    val start = LocalDate.now(zoneId)
    val end = start.plusDays(5)

    return Pair(start.toString(), end.toString())
}

