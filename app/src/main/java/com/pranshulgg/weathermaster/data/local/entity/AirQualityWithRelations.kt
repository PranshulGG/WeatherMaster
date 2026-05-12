package com.pranshulgg.weathermaster.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class AirQualityWithRelations(
    @Embedded val current: CurrentAirQualityEntity?
)