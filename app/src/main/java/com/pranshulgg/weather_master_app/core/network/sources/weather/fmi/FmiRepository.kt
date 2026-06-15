package com.pranshulgg.weather_master_app.core.network.sources.weather.fmi

import android.util.Log
import android.util.Xml
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResultType
import com.pranshulgg.weather_master_app.core.network.sources.weather.fmi.model.FmiWeather
import com.pranshulgg.weather_master_app.core.network.sources.weather.fmi.model.FmiWeatherMember
import com.pranshulgg.weather_master_app.core.utils.formatters.safeZoneId
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isWeatherCacheSafe
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnWeatherCache
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.fmi.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class FmiRepository @Inject constructor(
    val dao: LocationsDao,
    val weatherDao: WeatherDao,
    val api: FmiApi
) : WeatherRepository {

    // #HATE XML
    override suspend fun getWeather(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean
    ): WeatherResult =
        withContext(
            Dispatchers.IO
        ) {

            val cache = dao.getWeatherDataForLocation(location.id)

            val shouldReturnCache = shouldReturnWeatherCache(cache, isManualRefresh, isForceRefresh)

            when (shouldReturnCache) {
                WeatherResultType.REFRESH_TOO_EARLY -> return@withContext WeatherResult.RefreshNotAvailable
                WeatherResultType.SUCCESS -> return@withContext WeatherResult.Success(cache!!.toDomain())
                else -> {}
            }

            return@withContext try {
                val stationResponse = api.fetchStations()

                val stationBody =
                    stationResponse.body()?.byteStream()?.use { stream ->
                        fmiStationXml(stream, location)
                    }

                val forecastTimes = getStartEndTimeForecast(location)

                val response = api.fetchForecast(
                    latlon = "${location.latitude},${location.longitude}",
                    forecastTimes.second,
                    forecastTimes.first
                )

                val times = getStartEndTime()
                val currentResponse = if (stationBody != null) api.fetchCurrent(
                    stationBody,
                    times.first,
                    times.second
                ) else {
                    null
                }

                val currentBody = currentResponse?.body()?.byteStream()?.use { stream ->
                    fmiXml(stream)
                }


                if (!response.isSuccessful || response.body() == null) {
                    throw IllegalStateException("FMI request failed: ${response.code()}")
                }
                val body =
                    response.body()?.byteStream()?.use { stream ->
                        fmiXml(stream)
                    } ?: return@withContext WeatherResult.Error(exception = UnknownHostException())


                val final = FmiWeather(
                    data = body,
                    observation = currentBody
                )

                val domain = final.toDomain(location)

                weatherDao.insertWeather(
                    domain.current.toCurrentWeatherEntity(location.id),
                    domain.hourly.toHourlyWeatherEntity(location.id),
                    domain.daily.toDailyWeatherEntity(location.id),
                    location.id
                )
                WeatherResult.Success(domain)

            } catch (e: Exception) {

                val isCacheSafe = isWeatherCacheSafe(cache)

                WeatherResult.Error(
                    exception = e,
                    if (isCacheSafe) cache?.toDomain() else null
                )

            }
        }
}


private fun getStartEndTimeForecast(location: Location): Pair<String, String> {

    val zoneId = safeZoneId(location.timezone)

    val start = LocalDate.now(zoneId)
    val end = start.plusDays(5)

    return Pair(start.toString(), end.toString())
}

private fun getStartEndTime(): Pair<String, String> {

    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm':00Z'", Locale.ENGLISH)
    formatter.timeZone = TimeZone.getTimeZone("Etc/UTC")

    val start = System.currentTimeMillis().minus(1.hours.inWholeMilliseconds)
    val end = start.plus(10.minutes.inWholeMilliseconds)

    return Pair(formatter.format(Date(start)), formatter.format(Date(end)))
}


/**
 * We prase it manually
 * I couldn't find any better solution for ts
 * Retrofit said use "JAXB", I spent 2 hours and couldn't get it to work
 */
private fun fmiXml(stream: InputStream): List<FmiWeatherMember> {
    val result = mutableListOf<FmiWeatherMember>()
    val parser = Xml.newPullParser()
    parser.setInput(stream, null)

    var type = parser.eventType

    var currentTime: String? = null
    var currentName: String? = null
    var currentValue: String? = null

    // WE move by each tag and grab the content
    while (type != XmlPullParser.END_DOCUMENT) {

        when (type) {
            XmlPullParser.START_TAG -> {

                when (parser.name) {
                    "BsWfsElement" -> {
                        currentTime = null
                        currentName = null
                        currentValue = null
                    }

                    "Time" -> currentTime = parser.nextText()
                    "ParameterName" -> currentName = parser.nextText()
                    "ParameterValue" -> currentValue = parser.nextText()
                }
            }

            XmlPullParser.END_TAG -> {
                if (parser.name == "BsWfsElement") {
                    result.add(FmiWeatherMember(currentTime, currentName, currentValue))
                }
            }

        }

        type = parser.next()
    }
    return result
}

// Get the closest station
private fun fmiStationXml(stream: InputStream, location: Location): String? {
    val parser = Xml.newPullParser()
    parser.setInput(stream, null)

    var currentId: String? = null
    var currentStationId: String? = null
    var closestDistance = Float.MAX_VALUE

    var isAutomaticStation = false
    var currentLat: Double? = null
    var currentLon: Double? = null

    while (parser.eventType != XmlPullParser.END_DOCUMENT) {

        when (parser.eventType) {

            XmlPullParser.START_TAG -> {
                when (parser.name) {

                    "EnvironmentalMonitoringFacility" -> {
                        currentId = null
                        currentLat = null
                        currentLon = null
                        isAutomaticStation = false
                    }

                    "localId" -> {
                        currentId = parser.nextText()
                    }

                    "belongsTo" -> {
                        val title =
                            parser.getAttributeValue(
                                "http://www.w3.org/1999/xlink",
                                "title"
                            )

                        if (title == "Automaattinen sääasema") {
                            isAutomaticStation = true
                        }
                    }

                    "pos" -> {
                        val parts =
                            parser.nextText()
                                .trim()
                                .split(" ")

                        if (parts.size == 2) {
                            currentLat = parts[0].toDouble()
                            currentLon = parts[1].toDouble()
                        }
                    }
                }
            }

            XmlPullParser.END_TAG -> {
                if (parser.name == "EnvironmentalMonitoringFacility") {

                    if (
                        currentId != null &&
                        currentLat != null &&
                        currentLon != null &&
                        isAutomaticStation
                    ) {

                        val results = FloatArray(1)

                        android.location.Location.distanceBetween(
                            location.latitude,
                            location.longitude,
                            currentLat,
                            currentLon,
                            results
                        )

                        if (results[0] < closestDistance) {
                            closestDistance = results[0]
                            currentStationId = currentId
                        }

                    }
                }
            }
        }

        parser.next()
    }


    return currentStationId
}
