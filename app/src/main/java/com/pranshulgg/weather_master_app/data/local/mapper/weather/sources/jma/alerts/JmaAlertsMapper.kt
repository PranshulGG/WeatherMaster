package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.jma.alerts

import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.weather.alerts.AlertSeverity
import com.pranshulgg.weather_master_app.core.network.sources.weather.jma.alerts.json.JmaWarningJson
import java.time.OffsetDateTime

// Statuses seen live: "発表警報・注意報はなし" (none issued), "解除" (lifted) - both mean nothing
// active; anything else (e.g. "継続" continuing, "発表" newly issued) is an active warning.
private const val STATUS_NONE = "発表警報・注意報はなし"
private const val STATUS_LIFTED = "解除"

fun JmaWarningJson.toDomain(locationId: String, class10Code: String): List<Alert> {
    val reportTime = reportDatetime?.toEpochMillisOrNull()

    // areaTypes[0] is the class10-level group - the same granularity/codes this source's
    // weather side already resolves to, so class10Code matches an area code here directly.
    val area = areaTypes?.firstOrNull()?.areas?.firstOrNull { it.code == class10Code }
        ?: return emptyList()

    return area.warnings.orEmpty()
        .filter { it.status != STATUS_NONE && it.status != STATUS_LIFTED }
        .mapNotNull { warning ->
            val event = jmaWarningName(warning.code) ?: return@mapNotNull null
            Alert(
                locationId = locationId,
                event = event,
                severity = jmaWarningSeverity(warning.code),
                effective = reportTime,
                expires = null,
                description = headlineText?.trim().orEmpty(),
                source = publishingOffice?.trim(),
                lastUpdatedInMilli = System.currentTimeMillis()
            )
        }
}

// Transcribed from JMA's own warning-code enumeration (via Breezy Weather's JmaService.kt,
// same technique used to find the hourly-forecast endpoint - JMA doesn't publish this table
// itself). "+" suffix codes (e.g. "19+") are a slightly-elevated variant of the base code.
private fun jmaWarningName(code: String?): String? = when (code) {
    "33" -> "Heavy Rain Emergency Warning"
    "03" -> "Heavy Rain Warning"
    "10" -> "Heavy Rain Advisory"
    "04" -> "Flood Warning"
    "18" -> "Flood Advisory"
    "35" -> "Storm Emergency Warning"
    "05" -> "Storm Warning"
    "15" -> "Gale Advisory"
    "32" -> "Snowstorm Emergency Warning"
    "02" -> "Snowstorm Warning"
    "13" -> "Gale and Snow Advisory"
    "36" -> "Heavy Snow Emergency Warning"
    "06" -> "Heavy Snow Warning"
    "12" -> "Heavy Snow Advisory"
    "37" -> "High Wave Emergency Warning"
    "07" -> "High Wave Warning"
    "16" -> "High Wave Advisory"
    "38" -> "Storm Surge Emergency Warning"
    "08" -> "Storm Surge Warning"
    "19+", "19" -> "Storm Surge Advisory"
    "14" -> "Thunderstorm Advisory"
    "17" -> "Snow Melting Advisory"
    "20" -> "Dense Fog Advisory"
    "21" -> "Dry Air Advisory"
    "22" -> "Avalanche Advisory"
    "23" -> "Low Temperature Advisory"
    "24" -> "Frost Advisory"
    "25" -> "Ice Accretion Advisory"
    "26" -> "Snow Accretion Advisory"
    else -> null
}

private fun jmaWarningSeverity(code: String?): AlertSeverity = when (code) {
    "33" -> AlertSeverity.CRITICAL
    "35", "32", "36", "37", "38", "08" -> AlertSeverity.HIGH
    "03", "04", "05", "02", "06", "07", "19+" -> AlertSeverity.MODERATE
    "10", "18", "15", "13", "12", "16", "19", "14", "17",
    "20", "21", "22", "23", "24", "25", "26" -> AlertSeverity.LOW
    else -> AlertSeverity.UNKNOWN
}

private fun String.toEpochMillisOrNull(): Long? {
    return try {
        OffsetDateTime.parse(this).toInstant().toEpochMilli()
    } catch (e: Exception) {
        null
    }
}
