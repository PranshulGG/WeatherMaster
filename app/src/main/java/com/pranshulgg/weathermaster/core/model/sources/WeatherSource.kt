package com.pranshulgg.weathermaster.core.model.sources

enum class WeatherSource(val displayName: String) {
    OPEN_METEO("Open Meteo"),
    NWS("National Weather Service")
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