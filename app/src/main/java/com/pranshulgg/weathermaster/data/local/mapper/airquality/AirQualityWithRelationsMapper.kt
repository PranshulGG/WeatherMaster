package com.pranshulgg.weathermaster.data.local.mapper.airquality

import com.pranshulgg.weathermaster.core.model.domain.AirQuality
import com.pranshulgg.weathermaster.core.model.domain.AirQualityCurrent
import com.pranshulgg.weathermaster.data.local.entity.AirQualityWithRelations
import java.time.Instant


fun AirQualityWithRelations.toDomain(): AirQuality {
    return AirQuality(
        current = AirQualityCurrent(
            usAqi = this.current?.usAqi,
            pm10 = this.current?.pm10,
            pm25 = this.current?.pm25,
            carbonMonoxide = this.current?.carbonMonoxide,
            nitrogenDioxide = this.current?.nitrogenDioxide,
            sulphurDioxide = this.current?.sulphurDioxide,
            ozone = this.current?.ozone,
            lastUpdatedSecs = Instant.now().epochSecond
        )
    )
}
