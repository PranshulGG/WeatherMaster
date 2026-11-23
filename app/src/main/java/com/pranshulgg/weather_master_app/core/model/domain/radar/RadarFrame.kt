package com.pranshulgg.weather_master_app.core.model.domain.radar

/** One radar image time (a worldwide composite); its tiles are found by [path] */
class RadarFrame(val timeMilli: Long, val path: String)
