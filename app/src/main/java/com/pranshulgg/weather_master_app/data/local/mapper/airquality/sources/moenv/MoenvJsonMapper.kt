package com.pranshulgg.weather_master_app.data.local.mapper.airquality.sources.moenv

import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQualityCurrent
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.network.sources.airquality.moenv.json.MoenvStationJson
import com.pranshulgg.weather_master_app.core.utils.formatters.toSafeDouble


private fun ppbToMicrograms(ppb: Double?, molecularWeight: Double): Double? =
    ppb?.let { it * molecularWeight / 24.45 }

private fun ppmToMicrograms(ppm: Double?, molecularWeight: Double): Double? =
    ppm?.let { it * 1000 * molecularWeight / 24.45 }

fun findClosestStation(location: Location, stations: List<MoenvStationJson>): MoenvStationJson? {
    val results = FloatArray(1)

    return stations
        .mapNotNull { station ->
            val lat = station.latitude.toSafeDouble()
            val lon = station.longitude.toSafeDouble()
            if (lat == null || lon == null) return@mapNotNull null

            android.location.Location.distanceBetween(
                location.latitude, location.longitude, lat, lon, results
            )
            station to results[0]
        }
        .minByOrNull { it.second }
        ?.first
}

fun MoenvStationJson.toDomain(): AirQuality {
    return AirQuality(
        current = AirQualityCurrent(
            usAqi = aqi.toSafeDouble()?.toInt(),
            pm10 = pm10.toSafeDouble(),
            pm25 = pm25.toSafeDouble(),
            carbonMonoxide = ppmToMicrograms(co.toSafeDouble(), 28.01),
            nitrogenDioxide = ppbToMicrograms(no2.toSafeDouble(), 46.01),
            sulphurDioxide = ppbToMicrograms(so2.toSafeDouble(), 64.07),
            ozone = ppbToMicrograms(o3.toSafeDouble(), 48.00),
            lastUpdatedInMilli = System.currentTimeMillis()
        ),
        hourly = emptyList()
    )
}