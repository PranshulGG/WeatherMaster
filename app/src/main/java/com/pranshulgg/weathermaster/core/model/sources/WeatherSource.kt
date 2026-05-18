package com.pranshulgg.weathermaster.core.model.sources

enum class WeatherSource {
    OPEN_METEO
}


fun WeatherSource.toName(): String {
    return when (this) {
        WeatherSource.OPEN_METEO -> "Open Meteo"
    }
}

// WE MAP EVERY WEATHER SOURCE HERE, AS THEY GET ADDED
fun weatherProviderGetForCountry(countryCode: String): List<WeatherSource> {
    return when (countryCode) {
        "US" -> listOf(WeatherSource.OPEN_METEO)
        else -> emptyList() // IF NOT FOUND
    }
}