package com.pranshulgg.weather_master_app.core.network.sources.alerts.wmosevereweather

import android.util.Xml
import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResult
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResultType
import com.pranshulgg.weather_master_app.core.network.sources.alerts.wmosevereweather.model.WmoCapAlert
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnAlertsCache
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.sources.wmosevereweather.wmoSevereWeatherAlertsMapper
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.toEntity
import com.pranshulgg.weather_master_app.data.repository.AlertRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import javax.inject.Inject

class WmoSevereWeatherRepository @Inject constructor(
    private val api: WmoSevereWeatherApi,
    private val dao: AlertsDao,
    private val locationsDao: LocationsDao
) : AlertRepository {
    override suspend fun getAlerts(
        location: Location,
        isManualRefresh: Boolean,
        isForceRefresh: Boolean
    ): AlertResult = withContext(Dispatchers.IO) {

        val cache = dao.getAlertsForLocation(location.id)
        val shouldReturnCache = shouldReturnAlertsCache(
            cache,
            isManualRefresh,
            isForceRefresh,
            location.alertsLastFetchedAt
        )

        when (shouldReturnCache) {
            AlertResultType.RETURN_CACHE -> return@withContext AlertResult.Success(cache.map { it!!.toDomain() })
            else -> {}
        }

        val cqlFilter =
            "INTERSECTS(wkb_geometry, POINT (${location.latitude} ${location.longitude})) AND row_type NEQ 'BOUNDARY'"

        return@withContext try {

            val response = api.fetchAlerts(cqlFilter = cqlFilter)


            val body = response.body()
                ?: return@withContext AlertResult.Error(exception = AppException.Unknown())


            val alertsWithCap = body.features.filter {
                it.properties != null
            }.map { feature ->
                val url = feature.properties?.capUrl ?: feature.properties?.rLink

                val capAlert = if (!url.isNullOrBlank()) {
                    val responseAlert = api.fetchAlertsXml(url)

                    if (responseAlert.isSuccessful) {
                        responseAlert.body()?.byteStream()?.use { stream ->
                            parseAlertXmlBody(stream)
                        }
                    } else {
                        null
                    }
                } else {
                    null
                }

                feature to capAlert
            }


            val domain = wmoSevereWeatherAlertsMapper(alertsWithCap, location.id)

            dao.insertAlerts(
                domain.map { it.toEntity(location.id) },
                location.id
            )

            locationsDao.updateAlertsLastFetchedAt(location.id, System.currentTimeMillis())


            AlertResult.Success(domain)

        } catch (e: Exception) {
            AlertResult.Error(exception = e, cacheAlerts = cache.map { it!!.toDomain() })
        }


    }
}

private fun parseAlertXmlBody(stream: InputStream): WmoCapAlert {
    val parser = Xml.newPullParser()
    parser.setInput(stream, null)


    var type = parser.eventType

    var language: String? = null
    var event: String? = null
    var severity: String? = null
    var effective: String? = null
    var expires: String? = null
    var senderName: String? = null
    var description: String? = null
    var headline: String? = null

    while (type != XmlPullParser.END_DOCUMENT) {

        when (type) {
            XmlPullParser.START_TAG -> {
                when (parser.name) {
                    "language" -> language = parser.nextText()
                    "event" -> event = parser.nextText()
                    "severity" -> severity = parser.nextText()
                    "effective" -> effective = parser.nextText()
                    "expires" -> expires = parser.nextText()
                    "senderName" -> senderName = parser.nextText()
                    "description" -> description = parser.nextText()
                    "headline" -> headline = parser.nextText()
                }
            }
        }
        type = parser.next()
    }
    return WmoCapAlert(
        language = language,
        event = event,
        severity = severity,
        effective = effective,
        expires = expires,
        senderName = senderName,
        headline = headline,
        description = description
    )


}