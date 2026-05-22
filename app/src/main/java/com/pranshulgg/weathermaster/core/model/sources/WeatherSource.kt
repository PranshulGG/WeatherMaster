package com.pranshulgg.weathermaster.core.model.sources

enum class WeatherSource(
    val displayName: String,
    val hourlyAggregationLimitHours: Int,
    val displayLink: String
) {
    OPEN_METEO(
        displayName = "Open Meteo",
        hourlyAggregationLimitHours = 24,
        displayLink = "https://open-meteo.com/"
    ),
    NWS(
        displayName = "National Weather Service",
        hourlyAggregationLimitHours = 12,
        displayLink = "https://www.weather.gov/documentation/services-web-api"
    ),
    MET_NORWAY(
        displayName = "Met Norway",
        hourlyAggregationLimitHours = 12,
        "https://api.met.no/"
    );

    fun providesSnowFall(): Boolean {
        return when (this) {
            MET_NORWAY -> false
            else -> true
        }
    }
}


// WE MAP EVERY WEATHER SOURCE HERE, AS THEY GET ADDED
private val weatherSourcesByCountry = mapOf(
    "US" to listOf(WeatherSource.NWS)
)

fun getWeatherSourcesForCountry(countryCode: String?): List<WeatherSource> {
    return weatherSourcesByCountry[countryCode] ?: emptyList()
}


// GLOBAL SOURCES
private val weatherSourcesGlobal = listOf(WeatherSource.OPEN_METEO, WeatherSource.MET_NORWAY)

fun getWeatherSourcesGlobal(): List<WeatherSource> {
    return weatherSourcesGlobal
}