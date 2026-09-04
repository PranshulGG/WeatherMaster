package com.pranshulgg.weather_master_app.core.network.sources.weather.fmi

import android.util.Xml
import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.toAppException
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.FinishedWeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResult
import com.pranshulgg.weather_master_app.core.model.weather.WeatherResultType
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.network.sources.weather.fmi.model.FmiWeather
import com.pranshulgg.weather_master_app.core.network.sources.weather.fmi.model.FmiWeatherMember
import com.pranshulgg.weather_master_app.core.utils.formatters.safeZoneId
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnWeatherCache
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.mergeHourlyWeather
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.fmi.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.repository.weather.BaseWeatherRepository
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import com.pranshulgg.weather_master_app.data.repository.weather.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class FmiRepository @Inject constructor(
    val dao: WeatherContextDao,
    val weatherDao: WeatherDao,
    val api: FmiApi
) : BaseWeatherRepository() {

    override val weatherSource = Source.FMI

    override suspend fun fetchAndProcessWeather(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean,
        cacheModel: CacheModel
    ): Weather {
        val stationResponse = safeApiCall {
            api.fetchStations()
        }.getOrThrow()

        val stationBody = stationResponse.byteStream().use { stream ->
            fmiStationXml(stream, location)
        }

        val forecastTimes = getStartEndTimeForecast(location)

        val response = safeApiCall {
            api.fetchForecast(
                latlon = "${location.latitude},${location.longitude}",
                forecastTimes.second,
                forecastTimes.first
            )
        }.getOrThrow()

        val times = getStartEndTime()

        if (stationBody.isNullOrEmpty()) {
            throw AppException.EmptyResponseBody()
        }

        val currentResponse = safeApiCall {
            api.fetchCurrent(
                stationBody,
                times.first,
                times.second
            )
        }.getOrThrow()

        val currentBody = currentResponse.byteStream().use { stream ->
            fmiXml(stream)
        }

        val body = response.byteStream().use { stream ->
            fmiXml(stream)
        }


        val final = FmiWeather(
            data = body,
            observation = currentBody
        )

        return final.toDomain(location)
    }

    override suspend fun saveWeatherToDb(data: Weather, cacheModel: CacheModel) {
        useGenericSaveImplementation(cacheModel.cachedHourly, data, weatherDao)
    }

    override fun finishedWeatherResult(data: Weather): FinishedWeatherResult {
        return FinishedWeatherResult(weather = data)
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
