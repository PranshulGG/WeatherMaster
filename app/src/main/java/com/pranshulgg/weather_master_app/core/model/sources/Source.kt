package com.pranshulgg.weather_master_app.core.model.sources

import com.pranshulgg.weather_master_app.R


// TODO: implement alerts / air quality for sources that support it
enum class Source(
    val displayName: String,
    val hourlyAggregationLimitHours: Int = 24, // stupid and should be removed
    val displayLink: String,
    val fullName: String,
    val countryNameRes: Int? = null,
    val requiresUserApiKey: Boolean = false, // Source must not be selectable until the user has provided their API key
    val regionalButWorldwideSupport: Boolean = false,
    val capabilities: Set<Capability>,
//    val providesAlerts: Boolean = false,
//    val providesAirQuality: Boolean = false,
) {
    OPEN_METEO(
        displayName = "Open Meteo",
        fullName = "Open Meteo",
        displayLink = "https://open-meteo.com/",
        capabilities = setOf(Capability.WEATHER, Capability.AIR_QUALITY)
    ),
    NWS(
        displayName = "NWS",
        fullName = "National Weather Service",
        displayLink = "https://www.weather.gov/documentation/services-web-api",
        countryNameRes = R.string.country_usa,
        capabilities = setOf(Capability.WEATHER, Capability.ALERTS)
    ),
    SMHI(
        displayName = "SMHI",
        fullName = "Swedish Meteorological and Hydrological Institute",
        displayLink = "https://opendata.smhi.se",
        countryNameRes = R.string.country_sweden,
        capabilities = setOf(Capability.WEATHER)
    ),
    DWD(
        displayName = "DWD",
        fullName = "Bright Sky DWD",
        displayLink = "https://brightsky.dev",
        countryNameRes = R.string.country_germany,
        capabilities = setOf(Capability.WEATHER)
    ),
    METEO_FRANCE(
        displayName = "Météo-France",
        fullName = "Météo-France",
        displayLink = "https://meteofrance.com/",
        regionalButWorldwideSupport = true,
        capabilities = setOf(Capability.WEATHER)
    ),
    ECCC(
        displayName = "ECCC",
        fullName = "Environment and Climate Change Canada",
        displayLink = "https://app.weather.gc.ca/",
        countryNameRes = R.string.country_canada,
        capabilities = setOf(Capability.WEATHER)
    ),
    FMI(
        displayName = "FMI",
        fullName = "Finnish Meteorological Institute",
        displayLink = "https://en.ilmatieteenlaitos.fi/",
        countryNameRes = R.string.country_finland,
        capabilities = setOf(Capability.WEATHER)
    ),
    CHINA(
        displayName = "China",
        fullName = "China National Environmental Monitoring Centre",
        displayLink = "https://www.cnemc.cn/",
        capabilities = setOf(Capability.WEATHER)
    ),
    BMKG(
        displayName = "BMKG",
        fullName = "Badan Meteorologi, Klimatologi, dan Geofisika",
        displayLink = "https://www.bmkg.go.id/",
        countryNameRes = R.string.country_indonesia,
        capabilities = setOf(Capability.WEATHER)
    ),
    ACCU_WEATHER(
        displayName = "AccuWeather",
        fullName = "AccuWeather",
        displayLink = "https://www.accuweather.com/",
        capabilities = setOf(Capability.WEATHER, Capability.ALERTS, Capability.AIR_QUALITY)
    ),
    METEO_AM(
        displayName = "Meteo AM",
        fullName = "Meteorologia Aeronautica Militare",
        displayLink = "https://www.meteoam.it/",
        countryNameRes = R.string.country_italy,
        capabilities = setOf(Capability.WEATHER)
    ),
    IPMA(
        displayName = "IPMA",
        fullName = "Instituto Português do Mar e da Atmosfera",
        displayLink = "https://api.ipma.pt/",
        countryNameRes = R.string.country_portugal,
        capabilities = setOf(Capability.WEATHER)
    ),
    GISMETEO(
        displayName = "Gismeteo",
        fullName = "Gismeteo",
        displayLink = "https://www.gismeteo.ru/",
        countryNameRes = R.string.country_russia,
        regionalButWorldwideSupport = true,
        capabilities = setOf(Capability.WEATHER)
    ),
    MET_OFFICE(
        displayName = "Met Office",
        fullName = "Meteorological Office",
        displayLink = "https://www.metoffice.gov.uk/",
        countryNameRes = R.string.country_united_kingdom,
        requiresUserApiKey = true,
        regionalButWorldwideSupport = true,
        capabilities = setOf(Capability.WEATHER)
    ),
    PIRATE_WEATHER(
        displayName = "Pirate Weather",
        fullName = "Pirate Weather",
        displayLink = "https://pirateweather.net/",
        requiresUserApiKey = true,
        regionalButWorldwideSupport = false,
        capabilities = setOf(Capability.WEATHER, Capability.ALERTS)
    ),
    IMD(
        displayName = "IMD",
        fullName = "India Meteorological Department",
        displayLink = "https://mausam.imd.gov.in/",
        countryNameRes = R.string.country_india,
        capabilities = setOf(Capability.WEATHER)
    ),
    MET_NORWAY(
        displayName = "Met Norway",
        fullName = "Met Norway",
        displayLink = "https://api.met.no/",
        regionalButWorldwideSupport = true,
        capabilities = setOf(Capability.WEATHER)
    ),
    AEMET(
        displayName = "AEMET",
        fullName = "Agencia Estatal de Meteorología",
        displayLink = "https://opendata.aemet.es/centrodedescargas/altaUsuario",
        countryNameRes = R.string.country_spain,
        requiresUserApiKey = true,
        capabilities = setOf(Capability.WEATHER)
    ),

    WEATHER_API(
        displayName = "Weather API",
        fullName = "Weather API",
        displayLink = "https://www.weatherapi.com/",
        capabilities = setOf(Capability.ALERTS)
    ),
    WMO_SEVERE_WEATHER(
        displayName = "WMO Severe Weather",
        fullName = "WMO Severe Weather Information Centre",
        displayLink = "https://www.wmo.int/",
        capabilities = setOf(Capability.ALERTS)
    ),
    FPAS(
        displayName = "FOSS Public Alert Server",
        fullName = "FOSS Public Alert Server",
        displayLink = "https://invent.kde.org/webapps/foss-public-alert-server",
        capabilities = setOf(Capability.ALERTS)
    ),
    NONE(
        displayName = "None",
        fullName = "",
        displayLink = "",
        capabilities = setOf(Capability.ALERTS, Capability.AIR_QUALITY)
    ),
    CWA(
        displayName = "CWA",
        fullName = "Central Weather Administration",
        displayLink = "https://opendata.cwa.gov.tw/",
        countryNameRes = R.string.country_taiwan,
        requiresUserApiKey = true,
        capabilities = setOf(Capability.WEATHER)
    ),
    JMA(
        displayName = "JMA",
        fullName = "Japan Meteorological Agency",
        displayLink = "https://www.jma.go.jp/bosai/",
        countryNameRes = R.string.country_japan,
        capabilities = setOf(Capability.WEATHER, Capability.ALERTS)
    ),
    INMET(
        displayName = "INMET",
        fullName = "Instituto Nacional de Meteorologia",
        displayLink = "https://portal.inmet.gov.br/",
        countryNameRes = R.string.country_brazil,
        capabilities = setOf(Capability.WEATHER, Capability.ALERTS)
    ),

    OPEN_WEATHER(
        displayName = "OpenWeather",
        fullName = "OpenWeather",
        displayLink = "https://openweathermap.org/",
        capabilities = setOf(Capability.WEATHER, Capability.AIR_QUALITY),
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
            CWA -> false
            JMA -> false
            INMET -> false
            else -> true
        }
    }
}


// WE MAP EVERY WEATHER SOURCE HERE, AS THEY GET ADDED

private val sourcesByCountry = buildMap {
    put("US", listOf(Source.NWS))
    put("SE", listOf(Source.SMHI))
    put("DE", listOf(Source.DWD))
    put("CA", listOf(Source.ECCC))
    put("FI", listOf(Source.FMI))
    put("CN", listOf(Source.CHINA))
    put("ID", listOf(Source.BMKG))
    listOf("IT", "VA").forEach { put(it, listOf(Source.METEO_AM)) }
    put("PT", listOf(Source.IPMA))
    put("RU", listOf(Source.GISMETEO))
    listOf("GB", "UK").forEach { put(it, listOf(Source.MET_OFFICE)) }
    put("NO", listOf(Source.MET_NORWAY))
    put("FR", listOf(Source.METEO_FRANCE))
    put("ES", listOf(Source.AEMET))
    put("IN", listOf(Source.IMD))
    put("TW", listOf(Source.CWA))
    put("JP", listOf(Source.JMA))
    put("BR", listOf(Source.INMET))

}

fun getSourcesForCountry(countryCode: String?): List<Source> {
    return sourcesByCountry[countryCode] ?: emptyList()
}

/**
 * Any source that has global coverage,
 * including regional sources with global data,
 * must be added here.
 */
private val sourcesGlobal = listOf(

    // GLOBAL
    Source.OPEN_METEO,
    Source.ACCU_WEATHER,
    Source.PIRATE_WEATHER,
    Source.OPEN_WEATHER,

    // REGIONAL WITH GLOBAL
    Source.GISMETEO,
    Source.MET_OFFICE,
    Source.MET_NORWAY,
    Source.METEO_FRANCE,

    // ALERTS
    Source.WEATHER_API,
    Source.WMO_SEVERE_WEATHER,
    Source.FPAS,

    // Yes, "NONE" is a global source :P
    Source.NONE
)

fun getSourcesGlobal(): List<Source> {
    return sourcesGlobal
}

fun Source.isSourceSupportedFor(countryCode: String?): Boolean {
    val source = sourcesByCountry[countryCode] ?: return false

    return this in source
}

fun Source.isGlobal(): Boolean {
    return this in sourcesGlobal
}

enum class Capability {
    WEATHER,
    ALERTS,
    AIR_QUALITY
}