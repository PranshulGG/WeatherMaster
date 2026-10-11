package com.pranshulgg.weather_master_app.data.store

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

data class InitializationStoreState(
    val isInitialized: Boolean = false
)

@Singleton
class InitializationStore @Inject constructor() {

    private val _data = MutableStateFlow(InitializationStoreState())
    val data = _data.asStateFlow()

    fun setInitialized() {
        _data.update {
            it.copy(isInitialized = true)
        }
    }

    fun reset() {
        _data.update {
            it.copy(isInitialized = false)
        }
    }
}