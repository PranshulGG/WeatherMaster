package com.pranshulgg.weathermaster.core.utils

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class UuidGenerator {

    @OptIn(ExperimentalUuidApi::class)
    fun generateId(): String {
        return Uuid.random().toString()
    }

}