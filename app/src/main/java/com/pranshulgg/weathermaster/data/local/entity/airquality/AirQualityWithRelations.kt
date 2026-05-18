package com.pranshulgg.weathermaster.data.local.entity.airquality

import androidx.room.Embedded

data class AirQualityWithRelations(
    @Embedded val current: CurrentAirQualityEntity?
)