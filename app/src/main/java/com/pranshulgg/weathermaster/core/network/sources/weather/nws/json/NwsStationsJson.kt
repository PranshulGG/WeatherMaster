package com.pranshulgg.weathermaster.core.network.sources.weather.nws.json

import java.util.Properties


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
