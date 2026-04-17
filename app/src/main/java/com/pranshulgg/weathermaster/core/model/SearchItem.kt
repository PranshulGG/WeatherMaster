package com.pranshulgg.weathermaster.core.model

data class SearchItem(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val timezone: String,
    val countryCode: String?,
    val state: String,
)
