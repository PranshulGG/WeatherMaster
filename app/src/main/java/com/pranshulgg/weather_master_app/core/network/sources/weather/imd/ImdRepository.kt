package com.pranshulgg.weather_master_app.core.network.sources.weather.imd

import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.FinishedWeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResultType
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.network.sources.weather.imd.model.ImdForecastModel
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnWeatherCache
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.mergeHourlyWeather
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.imd.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.repository.weather.BaseWeatherRepository
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import com.pranshulgg.weather_master_app.data.repository.weather.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.roundToInt


class ImdRepository @Inject constructor(
    val dao: WeatherContextDao,
    val weatherDao: WeatherDao,
    val api: ImdApi
) : BaseWeatherRepository() {
    override val weatherSource = Source.IMD

    override suspend fun fetchAndProcessWeather(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean,
        cacheModel: CacheModel
    ): Weather {


        val imdTimeFrames = listOf("1hr", "3hr", "6hr")

        val timeStamps = imdTimeFrames.map {
            safeApiCall {
                api.fetchTimestamps("mmem_${it}.txt")
            }.getOrThrow()
        }

        val timeStampsBody = timeStamps.map {
            it.string().substringBefore(",")
        }
        val latitude = roundToEighth(location.latitude)
        val longitude = roundToEighth(location.longitude)

        val forecasts = timeStampsBody.mapIndexed { index, s ->
            safeApiCall {
                api.fetchForecast(
                    latitude = latitude,
                    longitude = longitude,
                    date = "${s}_${imdTimeFrames[index]}_0p125"
                )
            }.getOrThrow()
        }

        val final = ImdForecastModel(
            forecast1hr = forecasts[0],
            forecast3hr = forecasts[1],
            forecast6hr = forecasts[2],
            timeStamp1 = timeStampsBody[0],
            timeStamp2 = timeStampsBody[1],
            timeStamp3 = timeStampsBody[2]
        )

        val domain = final.toDomain(location)

        return domain

    }

    override suspend fun saveWeatherToDb(data: Weather, cacheModel: CacheModel) {
        val mergedHourly = mergeHourlyWeather(
            existing = cacheModel.cachedHourly,
            incoming = data.hourly.toHourlyWeatherEntity(data.location)
        )
        weatherDao.insertWeather(
            data.current.toCurrentWeatherEntity(data.location.id),
            mergedHourly,
            data.daily.toDailyWeatherEntity(data.location.id),
            data.location.id
        )
    }

    override fun finishedWeatherResult(data: Weather): FinishedWeatherResult {
        return FinishedWeatherResult(weather = data)
    }
}

private fun roundToEighth(value: Double): Double =
    (value * 8).roundToInt() / 8.0