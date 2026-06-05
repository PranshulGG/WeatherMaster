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
        hourlyAggregationLimitHours = 24,
        displayLink = "https://www.weather.gov/documentation/services-web-api"
    ),
    SMHI(
        displayName = "SMHI (Sweden)",
        hourlyAggregationLimitHours = 24,
        displayLink = "https://opendata.smhi.se"
    ),
    DWD(
        displayName = "Bright Sky DWD (Germany)",
        hourlyAggregationLimitHours = 24,
        displayLink = "https://brightsky.dev"
    ),
    METEO_FRANCE(
        displayName = "Météo-France",
        hourlyAggregationLimitHours = 24,
        displayLink = "https://meteofrance.com/"
    ),
    MET_NORWAY(
        displayName = "Met Norway",
        hourlyAggregationLimitHours = 24,
        "https://api.met.no/"
    );

    fun providesSnowFall(): Boolean {
        return when (this) {
            MET_NORWAY -> false
            DWD -> false
            else -> true
        }
    }
}


// WE MAP EVERY WEATHER SOURCE HERE, AS THEY GET ADDED
private val weatherSourcesByCountry = mapOf(
    "US" to listOf(WeatherSource.NWS),
    "SE" to listOf(WeatherSource.SMHI),
    "DE" to listOf(WeatherSource.DWD)
)

fun getWeatherSourcesForCountry(countryCode: String?): List<WeatherSource> {
    return weatherSourcesByCountry[countryCode] ?: emptyList()
}


// GLOBAL SOURCES
private val weatherSourcesGlobal = listOf(
    WeatherSource.OPEN_METEO, WeatherSource.MET_NORWAY,
    WeatherSource.METEO_FRANCE
)

fun getWeatherSourcesGlobal(): List<WeatherSource> {
    return weatherSourcesGlobal
}