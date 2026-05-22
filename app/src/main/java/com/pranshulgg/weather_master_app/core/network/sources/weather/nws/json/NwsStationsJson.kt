package com.pranshulgg.weather_master_app.core.network.sources.weather.nws.json


data class NwsStationsJson(
    val features: List<NwsStationsListJson>
)

data class NwsStationsListJson(
    val properties: NwsStationJson
)

data class NwsStationJson(
    val stationIdentifier: String,
    val distance: NwsStationDistanceJson?
)

data class NwsStationDistanceJson(
    val value: Double?
)
