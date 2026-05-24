package com.pranshulgg.weather_master_app.core.model.sources

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
    SMHI(
        displayName = "SMHI (Sweden)",
        hourlyAggregationLimitHours = 12,
        displayLink = "https://opendata.smhi.se"
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
    "US" to listOf(WeatherSource.NWS),
    "SE" to listOf(WeatherSource.SMHI)
)

fun getWeatherSourcesForCountry(countryCode: String?): List<WeatherSource> {
    return weatherSourcesByCountry[countryCode] ?: emptyList()
}


// GLOBAL SOURCES
private val weatherSourcesGlobal = listOf(WeatherSource.OPEN_METEO, WeatherSource.MET_NORWAY)

fun getWeatherSourcesGlobal(): List<WeatherSource> {
    return weatherSourcesGlobal
}