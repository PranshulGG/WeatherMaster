package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.accu.airquality

import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQualityCurrent
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQualityHourly
import com.pranshulgg.weather_master_app.core.network.sources.weather.accu.airquality.json.bundle.AccuAqiJsonBundle


fun AccuAqiJsonBundle.toDomain(): AirQuality {

    val current = this.current.data.pollutants.associate { it.type to it.concentration.value }
    val hourly = this.forecast.data

    return AirQuality(
        current = AirQualityCurrent(
            usAqi = null,
            pm10 = current["PM10"],
            pm25 = current["PM2_5"],
            carbonMonoxide = current["CO"],
            nitrogenDioxide = current["NO2"],
            sulphurDioxide = current["SO2"],
            ozone = current["O3"],
            lastUpdatedInMilli = System.currentTimeMillis()
        ),
        hourly = List(hourly.size) { index ->

            val pollutants =
                hourly[index].pollutants.associate { it.type to it.concentration.value }

            AirQualityHourly(
                time = hourly[index].timeSeconds,
                pm10 = pollutants["PM10"],
                pm25 = pollutants["PM2_5"],
                carbonMonoxide = pollutants["CO"],
                nitrogenDioxide = pollutants["NO2"],
                sulphurDioxide = pollutants["SO2"],
                ozone = pollutants["O3"]
            )
        }
    )
}