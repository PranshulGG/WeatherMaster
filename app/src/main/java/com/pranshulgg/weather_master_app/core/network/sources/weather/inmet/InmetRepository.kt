package com.pranshulgg.weather_master_app.core.network.sources.weather.inmet

import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.FinishedWeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherDataPack
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertsDataPack
import com.pranshulgg.weather_master_app.core.model.weather.alerts.FinishedAlertsResult
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.network.sources.weather.inmet.json.IbgeMunicipioJson
import com.pranshulgg.weather_master_app.core.network.sources.weather.inmet.json.InmetStationJson
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.entity.location.LocationKeyEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.inmet.InmetWeatherBundle
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.inmet.alerts.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.inmet.toDomain
import com.pranshulgg.weather_master_app.data.repository.alerts.AlertCacheModel
import com.pranshulgg.weather_master_app.data.repository.capability.AirQualityCapability
import com.pranshulgg.weather_master_app.data.repository.capability.AlertCapability
import com.pranshulgg.weather_master_app.data.repository.capability.WeatherCapability
import com.pranshulgg.weather_master_app.data.repository.data.BaseRepository
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Initial INMET integration implemented by https://github.com/altendorfme
 */
class InmetRepository @Inject constructor(
    val dao: WeatherContextDao,
    val weatherDao: WeatherDao,
    val forecastApi: InmetForecastApi,
    val observationApi: InmetObservationApi,
    val ibgeApi: IbgeApi,
    val avisosApi: InmetAvisosApi,
    val locationKeysDao: LocationKeysDao,
    val alertsDao: AlertsDao
) : BaseRepository() {

    @Volatile
    private var cachedStations: List<InmetStationJson>? = null

    override val weatherSource = Source.INMET
    override val alertSource = Source.INMET
    override val airQualitySource = Source.NONE

    override fun weatherCapability(): WeatherCapability? {
        return object : WeatherCapability {
            override suspend fun fetchAndProcess(
                location: Location,
                isManualRefresh: Boolean,
                isForceRefresh: Boolean,
                cacheModel: CacheModel
            ): WeatherDataPack {
                val ibgeCode = resolveIbgeCode(location)

                val forecastResponse = safeApiCall {
                    forecastApi.fetchForecast(ibgeCode)
                }.getOrThrow()

                val hourlyObservations = try {
                    val stationCode = resolveNearestStationCode(location)

                    if (stationCode != null) {
                        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

                        val result = safeApiCall {
                            observationApi.fetchHourlyData(today, today, stationCode)
                        }
                        result.getOrNull()?.takeIf { it.isNotEmpty() }
                    } else null
                } catch (e: Exception) {
                    null
                }

                val bundle = InmetWeatherBundle(
                    forecast = forecastResponse,
                    hourlyObservations = hourlyObservations
                )

                return WeatherDataPack(
                    weather = bundle.toDomain(location)
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

    override fun alertCapability(): AlertCapability? {
        return object : AlertCapability {
            override suspend fun fetchAndProcess(
                location: Location,
                isManualRefresh: Boolean,
                isForceRefresh: Boolean,
                alertCacheModel: AlertCacheModel
            ): AlertsDataPack {
                val ibgeCode = resolveIbgeCode(location)

                val avisos = safeApiCall {
                    avisosApi.fetchAvisos()
                }.getOrThrow()

                val domain = avisos.values.flatten().toDomain(location, ibgeCode)

                return AlertsDataPack(alerts = domain, location)
            }

            override suspend fun saveToDb(data: AlertsDataPack, alertCacheModel: AlertCacheModel) {
                useGenericSaveImplementationForAlerts(data, alertsDao, dao)
            }

            override fun finishedResult(data: List<Alert>): FinishedAlertsResult {
                return FinishedAlertsResult(alerts = data)
            }
        }
    }


    override fun airQualityCapability(): AirQualityCapability? = null

    private suspend fun resolveIbgeCode(location: Location): String {
        locationKeysDao.getCityKeyForLocation(location.id)?.cityKey?.let { return it }

        val stations = getStations()
        val nearestStation = stations?.findNearestStation(location.latitude, location.longitude)

        val uf = nearestStation?.sgEstado
            ?: inferUfFromState(location)

        val ibgeCode = if (uf != null) {
            val municipios = safeApiCall {
                ibgeApi.fetchMunicipiosByState(uf)
            }.getOrNull()

            municipios?.let { list ->
                val stationName = nearestStation?.dcNome
                val matchTarget = stationName ?: location.name

                list.bestMatch(matchTarget)?.id?.toString()
            }
        } else null

        val resolvedCode = ibgeCode ?: "3550308"

        locationKeysDao.insertCityKey(
            LocationKeyEntity(
                locationId = location.id,
                cityKey = resolvedCode
            )
        )

        return resolvedCode
    }

    private suspend fun resolveNearestStationCode(location: Location): String? {
        val stations = getStations() ?: return null
        return stations.findNearestStation(location.latitude, location.longitude)?.cdEstacao
    }

    private suspend fun getStations(): List<InmetStationJson>? {
        cachedStations?.let { return it }
        return try {
            val result = safeApiCall { observationApi.fetchStations() }
            val stations = result.getOrNull()
                ?.filter { it.cdSituacao == "Operante" && it.tpEstacao == "Automatica" }
                ?.filter {
                    it.vlLatitude.toDoubleOrNull() != null &&
                            it.vlLongitude.toDoubleOrNull() != null
                }
            stations?.let { cachedStations = it }
            stations
        } catch (e: Exception) {
            null
        }
    }

    private fun inferUfFromState(location: Location): String? {
        val stateLower = location.state.lowercase().trim()
        val stateMap = mapOf(
            "acre" to "AC", "alagoas" to "AL", "amapá" to "AP", "amazonas" to "AM",
            "bahia" to "BA", "ceará" to "CE", "distrito federal" to "DF",
            "espírito santo" to "ES", "goiás" to "GO", "maranhão" to "MA",
            "mato grosso" to "MT", "mato grosso do sul" to "MS",
            "minas gerais" to "MG", "pará" to "PA", "paraíba" to "PB",
            "paraná" to "PR", "pernambuco" to "PE", "piauí" to "PI",
            "rio de janeiro" to "RJ", "rio grande do norte" to "RN",
            "rio grande do sul" to "RS", "rondônia" to "RO", "roraima" to "RR",
            "santa catarina" to "SC", "são paulo" to "SP", "sergipe" to "SE",
            "tocantins" to "TO"
        )
        return stateMap[stateLower] ?: stateMap.entries.firstOrNull {
            stateLower.contains(it.key) || it.key.contains(stateLower)
        }?.value
    }

    private fun List<InmetStationJson>.findNearestStation(
        lat: Double,
        lon: Double
    ): InmetStationJson? {
        return minByOrNull { station ->
            val stationLat =
                station.vlLatitude.toDoubleOrNull() ?: return@minByOrNull Double.MAX_VALUE
            val stationLon =
                station.vlLongitude.toDoubleOrNull() ?: return@minByOrNull Double.MAX_VALUE
            haversineDistance(lat, lon, stationLat, stationLon)
        }
    }

    private fun List<IbgeMunicipioJson>.bestMatch(
        target: String
    ): IbgeMunicipioJson? {
        val normalizedTarget = stripAccents(target.lowercase().trim())
        return firstOrNull { stripAccents(it.nome.lowercase().trim()) == normalizedTarget }
            ?: firstOrNull { stripAccents(it.nome.lowercase().trim()).contains(normalizedTarget) }
            ?: firstOrNull { normalizedTarget.contains(stripAccents(it.nome.lowercase().trim())) }
            ?: minByOrNull {
                levenshteinDistance(normalizedTarget, stripAccents(it.nome.lowercase().trim()))
            }
    }

    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    private fun stripAccents(input: String): String {
        val normalized = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD)
        return normalized.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
    }

    private fun levenshteinDistance(a: String, b: String): Int {
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }
        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j
        for (i in 1..a.length) {
            for (j in 1..b.length) {
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + if (a[i - 1] == b[j - 1]) 0 else 1
                )
            }
        }
        return dp[a.length][b.length]
    }
}
