package com.pranshulgg.weather_master_app.core.model.sources

import android.content.Context
import com.pranshulgg.weather_master_app.R

enum class WeatherSource(
    val displayName: String,
    val hourlyAggregationLimitHours: Int = 24, // stupid and should be removed
    val displayLink: String,
    val fullName: String,
    val countryNameRes: Int? = null,
    val requiresUserApiKey: Boolean = false, // Source must not be selectable until the user has provided their API key
    val regionalButWorldwideSupport: Boolean = false
) {
    OPEN_METEO(
        displayName = "Open Meteo",
        fullName = "Open Meteo",
        displayLink = "https://open-meteo.com/"
    ),
    NWS(
        displayName = "NWS",
        fullName = "National Weather Service",
        displayLink = "https://www.weather.gov/documentation/services-web-api",
        countryNameRes = R.string.country_usa
    ),
    SMHI(
        displayName = "SMHI",
        fullName = "Swedish Meteorological and Hydrological Institute",
        displayLink = "https://opendata.smhi.se",
        countryNameRes = R.string.country_sweden
    ),
    DWD(
        displayName = "DWD",
        fullName = "Bright Sky DWD",
        displayLink = "https://brightsky.dev",
        countryNameRes = R.string.country_germany
    ),
    METEO_FRANCE(
        displayName = "Météo-France",
        fullName = "Météo-France",
        displayLink = "https://meteofrance.com/",
        regionalButWorldwideSupport = true
    ),
    ECCC(
        displayName = "ECCC",
        fullName = "Environment and Climate Change Canada",
        displayLink = "https://app.weather.gc.ca/",
        countryNameRes = R.string.country_canada
    ),
    FMI(
        displayName = "FMI",
        fullName = "Finnish Meteorological Institute",
        displayLink = "https://en.ilmatieteenlaitos.fi/",
        countryNameRes = R.string.country_finland
    ),
    CHINA(
        displayName = "China",
        fullName = "China National Environmental Monitoring Centre",
        displayLink = "https://www.cnemc.cn/"
    ),
    BMKG(
        displayName = "BMKG",
        fullName = "Badan Meteorologi, Klimatologi, dan Geofisika",
        displayLink = "https://www.bmkg.go.id/",
        countryNameRes = R.string.country_indonesia
    ),
    ACCU_WEATHER(
        displayName = "AccuWeather",
        fullName = "AccuWeather",
        displayLink = "https://www.accuweather.com/"
    ),
    METEO_AM(
        displayName = "Meteo AM",
        fullName = "Meteorologia Aeronautica Militare",
        displayLink = "https://www.meteoam.it/",
        countryNameRes = R.string.country_italy
    ),
    IPMA(
        displayName = "IPMA",
        fullName = "Instituto Português do Mar e da Atmosfera",
        displayLink = "https://api.ipma.pt/",
        countryNameRes = R.string.country_portugal
    ),
    GISMETEO(
        displayName = "Gismeteo",
        fullName = "Gismeteo",
        displayLink = "https://www.gismeteo.ru/",
        countryNameRes = R.string.country_russia,
        regionalButWorldwideSupport = true
    ),
    MET_OFFICE(
        displayName = "Met Office",
        fullName = "Meteorological Office",
        displayLink = "https://www.metoffice.gov.uk/",
        countryNameRes = R.string.country_united_kingdom,
        requiresUserApiKey = true,
        regionalButWorldwideSupport = true
    ),
    IMD(
        displayName = "IMD",
        fullName = "India Meteorological Department",
        displayLink = "https://mausam.imd.gov.in/",
        countryNameRes = R.string.country_india
    ),
    MET_NORWAY(
        displayName = "Met Norway",
        fullName = "Met Norway",
        displayLink = "https://api.met.no/",
        regionalButWorldwideSupport = true
    ),
    AEMET(
        displayName = "AEMET",
        fullName = "Agencia Estatal de Meteorología",
        displayLink = "https://opendata.aemet.es/centrodedescargas/altaUsuario",
        countryNameRes = R.string.country_spain,
        requiresUserApiKey = true
    );

    // Sources that provide snow/rain as precipitation
    fun providesSnowFall(): Boolean {
        return when (this) {
            MET_NORWAY -> false
            DWD -> false
            CHINA -> false
            BMKG -> false
            IMD -> false
            else -> true
        }
    }
}


// WE MAP EVERY WEATHER SOURCE HERE, AS THEY GET ADDED

private val weatherSourcesByCountry = buildMap {
    put("US", listOf(WeatherSource.NWS))
    put("SE", listOf(WeatherSource.SMHI))
    put("DE", listOf(WeatherSource.DWD))
    put("CA", listOf(WeatherSource.ECCC))
    put("FI", listOf(WeatherSource.FMI))
    put("CN", listOf(WeatherSource.CHINA))
    put("ID", listOf(WeatherSource.BMKG))
    listOf("IT", "VA").forEach { put(it, listOf(WeatherSource.METEO_AM)) }
    put("PT", listOf(WeatherSource.IPMA))
    put("RU", listOf(WeatherSource.GISMETEO))
    listOf("GB", "UK").forEach { put(it, listOf(WeatherSource.MET_OFFICE)) }
    put("NO", listOf(WeatherSource.MET_NORWAY))
    put("FR", listOf(WeatherSource.METEO_FRANCE))
    put("ES", listOf(WeatherSource.AEMET))
    put("IN", listOf(WeatherSource.IMD))

}

fun getWeatherSourcesForCountry(countryCode: String?): List<WeatherSource> {
    return weatherSourcesByCountry[countryCode] ?: emptyList()
}


// GLOBAL SOURCES
private val weatherSourcesGlobal = listOf(
    WeatherSource.OPEN_METEO,
    WeatherSource.MET_NORWAY,
    WeatherSource.METEO_FRANCE,
    WeatherSource.ACCU_WEATHER,
    WeatherSource.GISMETEO,
    WeatherSource.MET_OFFICE
)

fun getWeatherSourcesGlobal(): List<WeatherSource> {
    return weatherSourcesGlobal
}

fun WeatherSource.isSourceSupportedFor(countryCode: String?): Boolean {
    val source = weatherSourcesByCountry[countryCode] ?: return false

    return this in source
}

fun WeatherSource.isGlobal(): Boolean {
    return this in weatherSourcesGlobal
}