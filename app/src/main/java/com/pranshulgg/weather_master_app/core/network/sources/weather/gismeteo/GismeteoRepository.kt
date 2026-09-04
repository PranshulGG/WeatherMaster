package com.pranshulgg.weather_master_app.core.network.sources.weather.gismeteo

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
import com.pranshulgg.weather_master_app.core.network.sources.weather.gismeteo.model.GismeteoModel
import com.pranshulgg.weather_master_app.core.network.sources.weather.gismeteo.model.GismeteoModelCurrent
import com.pranshulgg.weather_master_app.core.network.sources.weather.gismeteo.model.GismeteoModelDaily
import com.pranshulgg.weather_master_app.core.network.sources.weather.gismeteo.model.GismeteoModelHourly
import com.pranshulgg.weather_master_app.core.utils.formatters.toSafeDouble
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnWeatherCache
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.mergeHourlyWeather
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationKeysDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherDao
import com.pranshulgg.weather_master_app.data.local.entity.location.LocationKeyEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.gismeteo.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toCurrentWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDailyWeatherEntity
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.weather.toHourlyWeatherEntity
import com.pranshulgg.weather_master_app.data.repository.weather.BaseWeatherRepository
import com.pranshulgg.weather_master_app.data.repository.weather.CacheModel
import com.pranshulgg.weather_master_app.data.repository.weather.WeatherAdditionalData
import com.pranshulgg.weather_master_app.data.repository.weather.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.math.roundToLong


class GismeteoRepository @Inject constructor(
    val dao: WeatherContextDao,
    val weatherDao: WeatherDao,
    val api: GismeteoApi,
    val locationKeysDao: LocationKeysDao
) : BaseWeatherRepository() {

    override val weatherSource = Source.GISMETEO

    override suspend fun fetchAndProcessWeather(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean,
        cacheModel: CacheModel
    ): Weather {
        var locationId = locationKeysDao.getCityKeyForLocation(location.id)
            ?.cityKey.toSafeDouble()
            ?.toLong()

        if (locationId == null) {
            locationId = safeApiCall {
                api.fetchLocations(location.latitude, location.longitude)
            }.getOrThrow().byteStream().use { stream ->
                findClosestLocation(location, stream)
            }
        }

        if (locationId == null) throw AppException.EmptyResponseBody()

        val response = api.fetchForecast(id = locationId)

        val body = response.body()?.byteStream()?.use { stream ->
            parseXml(stream)
        } ?: throw AppException.EmptyResponseBody()

        setAdditionalData(
            locationKey = locationId.toString()
        )

        return body.toDomain(location)
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

    override suspend fun saveAdditionalData(additionalData: WeatherAdditionalData, data: Weather) {
        locationKeysDao.insertCityKey(
            LocationKeyEntity(
                locationId = data.location.id,
                cityKey = additionalData.locationKey.toString()
            )
        )
    }

    override fun finishedWeatherResult(data: Weather): FinishedWeatherResult {
        return FinishedWeatherResult(weather = data)
    }
}

private fun findClosestLocation(location: Location, stream: InputStream): Long? {
    val parser = Xml.newPullParser()
    parser.setInput(stream, null)


    var type = parser.eventType
    var closestDistance = Float.MAX_VALUE
    var closestId: String? = null


    while (type != XmlPullParser.END_DOCUMENT) {
        if (type == XmlPullParser.START_TAG && parser.name == "item") {
            val lat = parser.getAttributeValue(null, "lat").toSafeDouble()
            val lon = parser.getAttributeValue(null, "lng").toSafeDouble()
            val id = parser.getAttributeValue(null, "id")

            if (lat != null && lon != null) {
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
        type = parser.next()

    }

    return closestId.toSafeDouble()?.roundToLong()
}

private fun parseXml(stream: InputStream): GismeteoModel {
    val parser = Xml.newPullParser()
    parser.setInput(stream, null)

    val dailyDates = mutableListOf<String>()
    val dailyTemperaturesMax = mutableListOf<Double?>()
    val dailyTemperaturesMin = mutableListOf<Double?>()
    val dailyPrecipitationTypes = mutableListOf<Int?>()
    val dailyPrecipitation = mutableListOf<Double?>()
    val dailyIcons = mutableListOf<String?>()
    val dailyWindDirections = mutableListOf<Int?>()


    var currentTemp: Double? = null
    var currentFeelsLike: Double? = null
    var currentPressure: Double? = null
    var currentWindSpeed: Double? = null
    var currentWindDirection: Int? = null
    var currentHumidity: Double? = null
    var currentIcon: String? = null
    var currentTime: String? = null

    var insideForecast: Boolean = false

    val hourlyTimes = mutableListOf<String>()
    val hourlyTemperatures = mutableListOf<Double?>()
    val hourlyPrecipitationTypes = mutableListOf<Int?>()
    val hourlyPrecipitation = mutableListOf<Double?>()
    val hourlyIcons = mutableListOf<String?>()
    val hourlyPressures = mutableListOf<Double?>()
    val hourlyWindSpeeds = mutableListOf<Double?>()
    val hourlyWindDirections = mutableListOf<Int?>()
    val hourlyHumidity = mutableListOf<Double?>()

    var type = parser.eventType
    while (type != XmlPullParser.END_DOCUMENT) {
        when (type) {
            XmlPullParser.START_TAG ->

                when (parser.name) {

                    "fact" -> {
                        currentTime =
                            parser.getAttributeValue(null, "valid")
                    }


                    "day" -> {
                        dailyDates.add(parser.getAttributeValue(null, "date"))
                        dailyTemperaturesMax.add(
                            parser.getAttributeValue(null, "tmax").toSafeDouble()
                        )
                        dailyTemperaturesMin.add(
                            parser.getAttributeValue(null, "tmin").toSafeDouble()
                        )
                        dailyPrecipitationTypes.add(
                            parser.getAttributeValue(null, "pt").toSafeDouble()?.toInt()
                        )
                        dailyPrecipitation.add(
                            parser.getAttributeValue(null, "prflt").toSafeDouble()
                        )
                        dailyIcons.add(parser.getAttributeValue(null, "icon"))
                        dailyWindDirections.add(
                            parser.getAttributeValue(null, "wd").toSafeDouble()?.toInt()
                        )
                    }

                    "forecast" -> {
                        insideForecast = true
                        hourlyTimes.add(parser.getAttributeValue(null, "valid"))
                    }


                    "values" -> {
                        if (insideForecast) {
                            hourlyTemperatures.add(
                                parser.getAttributeValue(null, "t").toSafeDouble()
                            )
                            hourlyPrecipitationTypes.add(
                                parser.getAttributeValue(null, "pt").toSafeDouble()?.toInt()
                            )
                            hourlyPrecipitation.add(
                                parser.getAttributeValue(null, "prflt").toSafeDouble()
                            )
                            hourlyIcons.add(parser.getAttributeValue(null, "icon"))
                            hourlyPressures.add(parser.getAttributeValue(null, "p").toSafeDouble())
                            hourlyWindSpeeds.add(
                                parser.getAttributeValue(null, "ws").toSafeDouble()
                            )
                            hourlyWindDirections.add(
                                parser.getAttributeValue(null, "wd").toSafeDouble()?.toInt()
                            )
                            hourlyHumidity.add(parser.getAttributeValue(null, "hum").toSafeDouble())

                        } else {
                            currentTemp = parser.getAttributeValue(null, "t").toSafeDouble()
                            currentFeelsLike = parser.getAttributeValue(null, "hi").toSafeDouble()
                            currentPressure = parser.getAttributeValue(null, "p").toSafeDouble()
                            currentWindSpeed = parser.getAttributeValue(null, "ws").toSafeDouble()
                            currentWindDirection =
                                parser.getAttributeValue(null, "wd").toSafeDouble()?.roundToInt()
                            currentHumidity = parser.getAttributeValue(null, "hum").toSafeDouble()
                            currentIcon = parser.getAttributeValue(null, "icon")
                        }
                    }
                }

            XmlPullParser.END_TAG -> {
                if (parser.name == "forecast") {
                    insideForecast = false
                }


            }
        }

        type = parser.next()

    }

    return GismeteoModel(
        current = GismeteoModelCurrent(
            temperature = currentTemp,
            feelsLike = currentFeelsLike,
            pressureMmHg = currentPressure,
            windSpeedMs = currentWindSpeed,
            windDirection = currentWindDirection,
            humidity = currentHumidity,
            icon = currentIcon,
            time = currentTime!!
        ),
        hourly = hourlyTimes.mapIndexed { index, t ->
            GismeteoModelHourly(
                temperature = hourlyTemperatures[index],
                pressureMmHg = hourlyPressures[index],
                windSpeedMs = hourlyWindSpeeds[index],
                humidity = hourlyHumidity[index],
                windDirection = hourlyWindDirections[index],
                icon = hourlyIcons[index],
                precipitationType = hourlyPrecipitationTypes[index],
                precipitation = hourlyPrecipitation[index],
                time = t
            )
        },
        daily = dailyDates.mapIndexed { index, d ->
            GismeteoModelDaily(
                temperatureMin = dailyTemperaturesMin[index],
                temperatureMax = dailyTemperaturesMax[index],
                icon = dailyIcons[index],
                precipitationType = dailyPrecipitationTypes[index],
                precipitation = dailyPrecipitation[index],
                time = d,
                windDirection = dailyWindDirections[index]
            )
        }
    )
}