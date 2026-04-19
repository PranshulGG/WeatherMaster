package com.pranshulgg.weathermaster.core.ui.state

import com.pranshulgg.weathermaster.core.model.Location
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ActiveLocationStore @Inject constructor() {

    private var _active = MutableStateFlow<Location?>(null)
    val active = _active.asStateFlow()


    fun set(location: Location) {
        _active.value = location
    }

}