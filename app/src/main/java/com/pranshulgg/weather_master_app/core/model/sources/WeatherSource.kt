package com.pranshulgg.weather_master_app.core.model.sources

enum class WeatherSource(
    val displayName: String,
    val hourlyAggregationLimitHours: Int = 24, // stupid and should be removed
    val displayLink: String,
    val fullName: String,
) {
    OPEN_METEO(
        displayName = "Open Meteo",
        fullName = "Open Meteo",
        displayLink = "https://open-meteo.com/"
    ),
    NWS(
        displayName = "NWS (United States)",
        fullName = "National Weather Service",
        displayLink = "https://www.weather.gov/documentation/services-web-api"
    ),
    SMHI(
        displayName = "SMHI (Sweden)",
        fullName = "Swedish Meteorological and Hydrological Institute",
        displayLink = "https://opendata.smhi.se"
    ),
    DWD(
        displayName = "DWD (Germany)",
        fullName = "Bright Sky DWD",
        displayLink = "https://brightsky.dev"
    ),
    METEO_FRANCE(
        displayName = "Météo-France",
        fullName = "Météo-France",
        displayLink = "https://meteofrance.com/"
    ),
    ECCC(
        displayName = "ECCC (Canada)",
        fullName = "Environment and Climate Change Canada",
        displayLink = "https://app.weather.gc.ca/",
    ),
    FMI(
        displayName = "FMI (Finland)",
        fullName = "Finnish Meteorological Institute",
        displayLink = "https://en.ilmatieteenlaitos.fi/"
    ),
    MET_NORWAY(
        displayName = "Met Norway",
        fullName = "Met Norway",
        displayLink = "https://api.met.no/"
    );

    // Sources that provide snow/rain as precipitation
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
    "DE" to listOf(WeatherSource.DWD),
    "CA" to listOf(WeatherSource.ECCC),
    "FI" to listOf(WeatherSource.FMI)
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