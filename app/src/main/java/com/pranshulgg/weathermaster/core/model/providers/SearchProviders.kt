package com.pranshulgg.weathermaster.core.model.providers

enum class SearchProviders {
    OPEN_METEO,
    GEO_NAMES
}

fun SearchProviders.toName(): String {
    return when (this) {
        SearchProviders.OPEN_METEO -> "Open Meteo"
        SearchProviders.GEO_NAMES -> "GeoNames"
    }
}