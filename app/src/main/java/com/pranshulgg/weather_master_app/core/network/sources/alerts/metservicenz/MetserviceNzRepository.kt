package com.pranshulgg.weather_master_app.core.network.sources.alerts.metservicenz

import android.util.Xml
import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertsDataPack
import com.pranshulgg.weather_master_app.core.model.weather.alerts.FinishedAlertsResult
import com.pranshulgg.weather_master_app.core.network.calls.safeApiCall
import com.pranshulgg.weather_master_app.core.network.sources.alerts.metservicenz.model.MetserviceNzCapAlert
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.weather.WeatherContextDao
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.sources.metservicenz.metserviceNzAlertsMapper
import com.pranshulgg.weather_master_app.data.repository.alerts.AlertCacheModel
import com.pranshulgg.weather_master_app.data.repository.capability.AirQualityCapability
import com.pranshulgg.weather_master_app.data.repository.capability.AlertCapability
import com.pranshulgg.weather_master_app.data.repository.capability.WeatherCapability
import com.pranshulgg.weather_master_app.data.repository.data.BaseRepository
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import javax.inject.Inject


/**
 * MetService NZ standalone CAP alerts integration implemented by https://github.com/reveler-hub
 */
class MetserviceNzRepository @Inject constructor(
    private val api: MetserviceNzApi,
    private val dao: AlertsDao,
    private val weatherContextDao: WeatherContextDao
) : BaseRepository() {

    override val alertSource = Source.METSERVICE_NZ
    override val weatherSource = Source.NONE
    override val airQualitySource = Source.NONE

    override fun alertCapability(): AlertCapability? {
        return object : AlertCapability {
            override suspend fun fetchAndProcess(
                location: Location,
                isManualRefresh: Boolean,
                isForceRefresh: Boolean,
                alertCacheModel: AlertCacheModel
            ): AlertsDataPack {

                val feedBody = safeApiCall { api.fetchAlertsFeed() }.getOrThrow()

                val alertLinks = feedBody.byteStream().use { parseCapFeedLinks(it) }

                // MetService's feed has no server-side geo filter (unlike NWS/FPAS), so
                // every active alert nationwide is fetched and matched client-side against
                // the location's point using the polygon(s) each CAP alert carries.
                val alerts = alertLinks.mapNotNull { link ->
                    val responseAlert = api.fetchAlertXml(link)

                    if (responseAlert.isSuccessful) {
                        responseAlert.body()?.byteStream()?.use { stream ->
                            parseAlertXmlBody(stream)
                        }
                    } else {
                        null
                    }
                }.filter { alert ->
                    alert.polygons.any { polygon ->
                        pointInPolygon(location.latitude, location.longitude, polygon)
                    }
                }

                val domain = metserviceNzAlertsMapper(alerts, location.id)

                return AlertsDataPack(domain, location)
            }

            override suspend fun saveToDb(data: AlertsDataPack, alertCacheModel: AlertCacheModel) {
                useGenericSaveImplementationForAlerts(data, alertsDao = dao, weatherContextDao)
            }

            override fun finishedResult(data: List<Alert>): FinishedAlertsResult {
                return FinishedAlertsResult(alerts = data)
            }
        }
    }

    override fun weatherCapability(): WeatherCapability? = null
    override fun airQualityCapability(): AirQualityCapability? = null
}

private fun parseCapFeedLinks(inputStream: InputStream): List<String> {
    val parser = Xml.newPullParser()
    parser.setInput(inputStream, null)

    val links = mutableListOf<String>()
    var type = parser.eventType
    var insideItem = false

    while (type != XmlPullParser.END_DOCUMENT) {
        when (type) {
            XmlPullParser.START_TAG -> {
                when (parser.name) {
                    "item" -> insideItem = true
                    "link" -> if (insideItem) links.add(parser.nextText().trim())
                }
            }

            XmlPullParser.END_TAG -> {
                if (parser.name == "item") insideItem = false
            }
        }
        type = parser.next()
    }
    return links
}

private fun parseAlertXmlBody(stream: InputStream): MetserviceNzCapAlert {
    val parser = Xml.newPullParser()
    parser.setInput(stream, null)

    var type = parser.eventType

    var event: String? = null
    var severity: String? = null
    var onset: String? = null
    var expires: String? = null
    var senderName: String? = null
    var description: String? = null
    var headline: String? = null
    var inInfo = false
    var infoCaptured = false

    val polygons = mutableListOf<List<Pair<Double, Double>>>()

    while (type != XmlPullParser.END_DOCUMENT) {
        when (type) {
            XmlPullParser.START_TAG -> {
                when (parser.name) {
                    // a CAP alert can carry multiple <info> blocks (e.g. per language);
                    // MetService's feed is English-only, so we just take the first one
                    "info" -> inInfo = !infoCaptured

                    "event" -> if (inInfo) event = parser.nextText()
                    "severity" -> if (inInfo) severity = parser.nextText()
                    "onset" -> if (inInfo) onset = parser.nextText()
                    "expires" -> if (inInfo) expires = parser.nextText()
                    "senderName" -> if (inInfo) senderName = parser.nextText()
                    "description" -> if (inInfo) description = parser.nextText()
                    "headline" -> if (inInfo) headline = parser.nextText()
                    "polygon" -> if (inInfo) polygons.add(parsePolygon(parser.nextText()))
                }
            }

            XmlPullParser.END_TAG -> {
                if (parser.name == "info" && inInfo) {
                    infoCaptured = true
                    inInfo = false
                }
            }
        }
        type = parser.next()
    }

    return MetserviceNzCapAlert(
        event = event,
        severity = severity,
        onset = onset,
        expires = expires,
        senderName = senderName,
        headline = headline,
        description = description,
        polygons = polygons
    )
}

// CAP polygon format: whitespace-separated "lat,lon" pairs forming a closed ring.
private fun parsePolygon(raw: String): List<Pair<Double, Double>> {
    return raw.trim().split(Regex("\\s+")).mapNotNull { pair ->
        val parts = pair.split(",")
        if (parts.size != 2) return@mapNotNull null
        val lat = parts[0].toDoubleOrNull() ?: return@mapNotNull null
        val lon = parts[1].toDoubleOrNull() ?: return@mapNotNull null
        lat to lon
    }
}

// Standard ray-casting point-in-polygon test.
private fun pointInPolygon(lat: Double, lon: Double, polygon: List<Pair<Double, Double>>): Boolean {
    if (polygon.size < 3) return false

    var inside = false
    var j = polygon.size - 1
    for (i in polygon.indices) {
        val (latI, lonI) = polygon[i]
        val (latJ, lonJ) = polygon[j]
        if ((lonI > lon) != (lonJ > lon) &&
            lat < (latJ - latI) * (lon - lonI) / (lonJ - lonI) + latI
        ) {
            inside = !inside
        }
        j = i
    }
    return inside
}
