package com.pranshulgg.weather_master_app.core.network.sources.weather.jma.alerts.json

// areaTypes[0] uses the same class10 codes the weather source already resolves/caches (e.g.
// "130010"), so no extra location resolution is needed to find "our" area in the response.
data class JmaWarningJson(
    val reportDatetime: String?,
    val publishingOffice: String?,
    val headlineText: String?,
    val areaTypes: List<JmaWarningAreaTypeJson>?
)

data class JmaWarningAreaTypeJson(
    val areas: List<JmaWarningAreaJson>?
)

data class JmaWarningAreaJson(
    val code: String?,
    val warnings: List<JmaWarningEntryJson>?
)

data class JmaWarningEntryJson(
    val code: String?,
    val status: String?
)
