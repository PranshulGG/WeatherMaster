package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.inmet.alerts

import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertSeverity
import com.pranshulgg.weather_master_app.core.network.sources.weather.inmet.json.InmetAvisoJson
import com.pranshulgg.weather_master_app.core.utils.formatters.safeZoneId
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val INMET_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
private val IBGE_CODE_REGEX = Regex("""\((\d{4,})\)""")

fun List<InmetAvisoJson>.toDomain(location: Location, ibgeCode: String): List<Alert> {
    if (isEmpty()) return emptyList()
    val zone = safeZoneId(location.timezone)
    val seen = mutableSetOf<Long>()
    return mapNotNull { aviso ->
        val key = aviso.idAviso ?: aviso.id ?: return@mapNotNull null
        if (!seen.add(key)) return@mapNotNull null
        aviso.toAlert(location.id, ibgeCode, zone)
    }
}

private fun InmetAvisoJson.toAlert(
    locationId: String,
    ibgeCode: String,
    zone: ZoneId
): Alert? {
    if (!matchesIbgeCode(ibgeCode)) return null

    val event = descricao?.trim()?.takeIf { it.isNotEmpty() } ?: "Aviso INMET"
    val severity = inmetSeverity(severidade, idSeveridade)
    val description = buildDescription(descricao, severidade, riscos, instrucoes)

    return Alert(
        locationId = locationId,
        event = event,
        severity = severity,
        effective = inicio?.toEpochMillis(zone),
        expires = fim?.toEpochMillis(zone),
        description = description,
        source = "INMET",
        lastUpdatedInMilli = System.currentTimeMillis()
    )
}

private fun InmetAvisoJson.matchesIbgeCode(ibgeCode: String): Boolean {
    geocodes?.split(",")?.mapNotNull { it.trim().takeIf { c -> c.isNotEmpty() } }
        ?.let { codes -> if (ibgeCode in codes) return true }

    municipios?.let { m ->
        val codes = IBGE_CODE_REGEX.findAll(m).map { it.groupValues[1] }.toList()
        if (ibgeCode in codes) return true
    }

    return false
}

private fun inmetSeverity(severidade: String?, idSeveridade: Int?): AlertSeverity? {
    val normalized = severidade?.trim()?.lowercase()
    return when {
        normalized == null && idSeveridade == null -> null
        normalized?.contains("iminen") == true -> AlertSeverity.CRITICAL
        normalized == "perigo" || idSeveridade == 7 -> AlertSeverity.HIGH
        normalized?.contains("potencial") == true || idSeveridade == 6 -> AlertSeverity.MODERATE
        else -> AlertSeverity.UNKNOWN
    }
}

private fun buildDescription(
    descricao: String?,
    severidade: String?,
    riscos: String?,
    instrucoes: String?
): String {
    return buildString {
        descricao?.trim()?.takeIf { it.isNotEmpty() }?.let { append(it) }
        severidade?.trim()?.takeIf { it.isNotEmpty() }?.let {
            if (isNotEmpty()) append(" — ")
            append("Severidade: ").append(it)
        }
        riscos?.trim()?.takeIf { it.isNotEmpty() }?.let { append("\n\nRiscos: ").append(it) }
        instrucoes?.trim()?.takeIf { it.isNotEmpty() }?.let { append("\n\nInstruções: ").append(it) }
    }.trim()
}

private fun String.toEpochMillis(zone: ZoneId): Long? {
    return runCatching {
        LocalDateTime.parse(this, INMET_DATETIME_FORMAT)
            .atZone(zone)
            .toInstant()
            .toEpochMilli()
    }.getOrNull()
}
