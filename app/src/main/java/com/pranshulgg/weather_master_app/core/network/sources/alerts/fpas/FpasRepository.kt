package com.pranshulgg.weather_master_app.core.network.sources.alerts.fpas

import android.util.Xml
import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResult
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertResultType
import com.pranshulgg.weather_master_app.core.network.sources.alerts.fpas.model.FpasCapAlert
import com.pranshulgg.weather_master_app.core.utils.locale.getCurrentAppLocale
import com.pranshulgg.weather_master_app.core.utils.weather.cache.shouldReturnAlertsCache
import com.pranshulgg.weather_master_app.data.local.dao.alerts.AlertsDao
import com.pranshulgg.weather_master_app.data.local.dao.location.LocationsDao
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.sources.fpas.fpasAlertsMapper
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.toDomain
import com.pranshulgg.weather_master_app.data.local.mapper.alerts.toEntity
import com.pranshulgg.weather_master_app.data.repository.data.AlertRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import javax.inject.Inject


class FpasRepository @Inject constructor(
    private val api: FpasApi,
    private val dao: AlertsDao,
    private val locationsDao: LocationsDao
) : AlertRepository {

    override val alertSource = Source.FPAS


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

        return@withContext try {

            val response = api.fetchAlerts(
                minLat = location.latitude - 0.1,
                maxLat = location.latitude + 0.1,
                minLon = location.longitude - 0.1,
                maxLon = location.longitude + 0.1
            )

            val body = response.body()
                ?: return@withContext AlertResult.Error(
                    exception = AppException.Unknown(),
                    cacheAlerts = cache.map { it!!.toDomain() })


            val preferredLanguage = getCurrentAppLocale().language

            val alerts = body.mapNotNull {
                val responseAlert = api.fetchAlertsCap(it)

                if (responseAlert.isSuccessful) {
                    responseAlert.body()?.byteStream()?.use { stream ->
                        parseAlertXmlBody(stream, preferredLanguage)
                    }
                } else {
                    null
                }
            }


            val domain = fpasAlertsMapper(alerts, location.id)

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

private fun parseAlertXmlBody(stream: InputStream, preferredLanguage: String): FpasCapAlert {
    val parser = Xml.newPullParser()
    parser.setInput(stream, null)

    val infoBlocks = mutableListOf<FpasCapAlert>()

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
                    // a CAP alert can carry one <info> block per language; reset per block
                    // so fields from one language don't leak into another
                    "info" -> {
                        language = null
                        event = null
                        severity = null
                        effective = null
                        expires = null
                        senderName = null
                        description = null
                        headline = null
                    }

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

            XmlPullParser.END_TAG -> {
                if (parser.name == "info") {
                    infoBlocks += FpasCapAlert(
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
            }
        }
        type = parser.next()
    }

    return selectPreferredInfoBlock(infoBlocks, preferredLanguage)
}

private fun selectPreferredInfoBlock(
    infoBlocks: List<FpasCapAlert>,
    preferredLanguage: String
): FpasCapAlert {
    return infoBlocks.firstOrNull {
        it.language?.startsWith(
            preferredLanguage,
            ignoreCase = true
        ) == true
    }
        ?: infoBlocks.firstOrNull { it.language?.startsWith("en", ignoreCase = true) == true }
        ?: infoBlocks.firstOrNull()
        ?: FpasCapAlert(
            language = null,
            event = null,
            severity = null,
            effective = null,
            expires = null,
            senderName = null,
            headline = null,
            description = null
        )
}