package com.pranshulgg.weather_master_app.core.network.sources.weather.aemet

import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.FinishedWeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherDataPack
import com.pranshulgg.weather_master_app.core.network.sources.weather.aemet.json.AemetMunicipioJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.aemet.model.AemetForecastJson
import com.pranshulgg.weather_master_app.core.utils.formatters.toSafeDouble
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.ApiKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.entity.location.LocationKeyEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.aemet.toDomain
import com.pranshulgg.weather_master_app.data.repository.capability.AirQualityCapability
import com.pranshulgg.weather_master_app.data.repository.capability.AlertCapability
import com.pranshulgg.weather_master_app.data.repository.capability.WeatherCapability
import com.pranshulgg.weather_master_app.data.repository.data.BaseRepository
import com.pranshulgg.weather_master_app.data.repository.data.WeatherAdditionalData
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

/**
 * Initial AEMET integration implemented by https://github.com/reveler-hub
 */
class AemetRepository @Inject constructor(
    val dao: WeatherContextDao,
    val weatherDao: WeatherDao,
    val api: AemetApi,
    val apiKeysDao: ApiKeysDao,
    val locationKeysDao: LocationKeysDao
) : BaseRepository() {

    override val weatherSource = Source.AEMET
    override val alertSource = Source.NONE
    override val airQualitySource = Source.NONE

    override fun weatherCapability(): WeatherCapability? {
        return object : WeatherCapability {
            override suspend fun fetchAndProcess(
                location: Location,
                isManualRefresh: Boolean,
                isForceRefresh: Boolean,
                cacheModel: CacheModel
            ): WeatherDataPack {

                val key = cacheModel.apiKey!!

                val municipio = locationKeysDao.getCityKeyForLocation(location.id)?.cityKey
                    ?: resolveMunicipio(location, key)
                    ?: throw AppException.Unknown()

                val dailyEnvelope = safeCall {
                    api.fetchDailyForecastEnvelope(municipio, key)
                }.getOrThrow()

                val dailyDatosUrl = dailyEnvelope.datos ?: throw AppException.Unknown()

                val daily = safeCall {
                    api.fetchDailyForecastData(dailyDatosUrl)
                }.getOrThrow().firstOrNull()
                    ?: throw AppException.EmptyResponseBody()


                val hourlyEnvelope = safeCall {
                    api.fetchHourlyForecastEnvelope(municipio, key)
                }.getOrThrow()

                val hourlyDatosUrl = hourlyEnvelope.datos ?: throw AppException.EmptyResponseBody()


                val hourly = safeCall {
                    api.fetchHourlyForecastData(hourlyDatosUrl)
                }.getOrThrow().firstOrNull()
                    ?: throw AppException.EmptyResponseBody()

                val domain = AemetForecastJson(daily = daily, hourly = hourly).toDomain(location)


                return WeatherDataPack(
                    weather = domain,
                    additionalData = WeatherAdditionalData(locationKey = municipio)
                )
            }

            override suspend fun saveAdditionalDataToDb(pack: WeatherDataPack) {
                locationKeysDao.insertCityKey(
                    LocationKeyEntity(
                        locationId = pack.weather.location.id,
                        cityKey = pack.additionalData?.locationKey!!
                    )
                )
            }

            override suspend fun saveToDb(data: WeatherDataPack, cacheModel: CacheModel) {
                useGenericSaveImplementationForWeather(
                    existingHourly = cacheModel.cachedHourly,
                    data.weather,
                    weatherDao
                )
            }

            override fun finishedResult(data: Weather): FinishedWeatherResult {
                return FinishedWeatherResult(weather = data)
            }
        }
    }

    override fun airQualityCapability(): AirQualityCapability? = null
    override fun alertCapability(): AlertCapability? = null

    private suspend fun resolveMunicipio(location: Location, apiKey: String): String? {

        val envelope = safeCall {
            api.fetchMunicipiosEnvelope(apiKey)
        }.getOrElse { return null }

        val datosUrl = envelope.datos ?: return null
        val municipios = api.fetchMunicipiosData(datosUrl).body() ?: return null

        return getClosestMunicipio(municipios, location)?.removePrefix("id")
    }
}

private fun getClosestMunicipio(municipios: List<AemetMunicipioJson>, location: Location): String? {
    var closestDistance = Float.MAX_VALUE
    var closestId: String? = null

    for (municipio in municipios) {
        val lat = municipio.latitud_dec.toSafeDouble()
        val lon = municipio.longitud_dec.toSafeDouble()
        val id = municipio.id

        if (lat != null && lon != null && id != null) {
            val results = FloatArray(1)

            android.location.Location.distanceBetween(
                location.latitude,
                location.longitude,
                lat,
                lon,
                results
            )

            if (results[0] < closestDistance) {
                closestDistance = results[0]
                closestId = id
            }
        }
    }

    return closestId
}

private suspend fun <T> safeCall(
    call: suspend () -> Response<T>
): Result<T> {
    return try {
        val response = call()
        response.body()?.let(Result.Companion::success)
            ?: Result.failure(
                AppException.EmptyResponseBody()
            )
    } catch (e: Exception) {
        if (e is HttpException && e.code() == 401) {
            return Result.failure(AppException.ApiKeyRejectedError())
        }
        Result.failure(e.toAppException())
    }
}