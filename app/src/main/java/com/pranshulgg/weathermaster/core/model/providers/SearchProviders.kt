package com.pranshulgg.weathermaster.core.model.providers

enum class SearchProvider {
    OPEN_METEO,
    GEO_NAMES
}

fun SearchProvider.toName(): String {
    return when (this) {
        SearchProvider.OPEN_METEO -> "Open Meteo"
        SearchProvider.GEO_NAMES -> "GeoNames"
    }
}