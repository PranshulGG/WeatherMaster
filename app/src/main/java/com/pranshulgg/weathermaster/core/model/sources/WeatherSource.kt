package com.pranshulgg.weathermaster.core.model.sources

enum class WeatherSource(val displayName: String, val hourlyAggregationLimitHours: Int) {
    OPEN_METEO(displayName = "Open Meteo", hourlyAggregationLimitHours = 24),
    NWS(displayName = "National Weather Service", hourlyAggregationLimitHours = 12);
}


// WE MAP EVERY WEATHER SOURCE HERE, AS THEY GET ADDED
private val weatherSourcesByCountry = mapOf(
    "US" to listOf(WeatherSource.NWS) // TODO: WEATHER SOURCE NOT IMPLEMENTED
)

fun getWeatherSourcesForCountry(countryCode: String?): List<WeatherSource> {
    return weatherSourcesByCountry[countryCode] ?: emptyList()
}


// GLOBAL SOURCES
private val weatherSourcesGlobal = listOf(WeatherSource.OPEN_METEO)

fun getWeatherSourcesGlobal(): List<WeatherSource> {
    return weatherSourcesGlobal
}