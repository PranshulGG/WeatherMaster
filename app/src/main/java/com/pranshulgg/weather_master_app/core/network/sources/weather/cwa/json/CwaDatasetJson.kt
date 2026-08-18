package com.pranshulgg.weather_master_app.core.network.sources.weather.cwa.json

data class CwaDatasetJson(
    val success: String?,
    val records: CwaRecordsJson?
)

data class CwaRecordsJson(
    val Locations: List<CwaLocationsGroupJson>?
)

data class CwaLocationsGroupJson(
    val LocationsName: String?,
    val Location: List<CwaLocationJson>?
)

data class CwaLocationJson(
    val LocationName: String?,
    val Geocode: String?,
    val Latitude: String?,
    val Longitude: String?,
    val WeatherElement: List<CwaWeatherElementJson>?
)

data class CwaWeatherElementJson(
    val ElementName: String?,
    val Time: List<CwaTimeEntryJson>?
)

// CWA mixes two shapes under the same "Time" key depending on the element: point-in-time
// entries (DataTime) for e.g. temperature/humidity, and interval entries (StartTime/EndTime)
// for e.g. condition/precipitation-probability. ElementValue is always a small map of 1-2
// string fields keyed by name (e.g. {"Temperature": "29"}) - modeled generically here rather
// than with one data class per ElementName, since ~28 distinct value keys appear across ~15
// element names.
data class CwaTimeEntryJson(
    val DataTime: String?,
    val StartTime: String?,
    val EndTime: String?,
    val ElementValue: List<Map<String, String>>?
)
