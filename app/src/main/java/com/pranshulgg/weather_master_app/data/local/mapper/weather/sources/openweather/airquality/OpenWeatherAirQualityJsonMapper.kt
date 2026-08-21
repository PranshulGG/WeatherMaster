package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.openweather.airquality

import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQualityCurrent
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQualityHourly
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.network.sources.weather.openweather.json.OpenWeatherAirQualityJson
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.secondsToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.formatters.getCurrentTimeFor
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findHourlyIndexForTime


fun OpenWeatherAirQualityJson.toDomain(location: Location): AirQuality {

    val zoneId = location.timezone

    val currentHourIndex = findHourlyIndexForTime(
        list.map { it.dt.secondsToMilliseconds() },
        startMilli = getCurrentTimeFor(zoneId)
    )

    val current = list[currentHourIndex].components


    return AirQuality(
        current = AirQualityCurrent(
            usAqi = null,
            pm10 = current.pm10,
            pm25 = current.pm2_5,
            carbonMonoxide = current.co,
            nitrogenDioxide = current.no2,
            sulphurDioxide = current.so2,
            ozone = current.o3,
            lastUpdatedInMilli = System.currentTimeMillis(),
        ),
        hourly = list.map {
            AirQualityHourly(
                pm10 = it.components.pm10,
                pm25 = it.components.pm2_5,
                carbonMonoxide = it.components.co,
                nitrogenDioxide = it.components.no2,
                sulphurDioxide = it.components.so2,
                ozone = it.components.o3,
                time = it.dt
            )
        }
    )
}
