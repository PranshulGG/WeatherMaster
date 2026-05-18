package com.pranshulgg.weathermaster.core.model.sources

enum class SearchSource {
    OPEN_METEO,
    GEO_NAMES
}

fun SearchSource.toName(): String {
    return when (this) {
        SearchSource.OPEN_METEO -> "Open Meteo"
        SearchSource.GEO_NAMES -> "GeoNames"
    }
}