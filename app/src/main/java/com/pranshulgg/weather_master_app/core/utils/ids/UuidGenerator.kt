package com.pranshulgg.weather_master_app.core.utils.ids

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object UuidGenerator {

    @OptIn(ExperimentalUuidApi::class)
    fun generateId(): String {
        return Uuid.random().toString()
    }

}