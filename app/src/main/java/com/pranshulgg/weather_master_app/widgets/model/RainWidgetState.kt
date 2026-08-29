package com.pranshulgg.weather_master_app.widgets.model

import kotlinx.serialization.Serializable

/**
 * Rain bars for the homescreen rain widget. Sourced from the high-resolution
 * nowcast when available, else from the hourly model precipitation (the same
 * fallback the in-app "Highlight rain" option uses).
 */
@Serializable
data class RainWidgetState(
    /** Short unit label, e.g. "mm". */
    val unitLabel: String = "",
    /** Display name of the data source, e.g. "Buienradar". */
    val sourceLabel: String = "",
    /** True when the bars come from the radar nowcast (5-min steps). */
    val isNowcast: Boolean = false,
    /** Bars: pre-formatted time label + precipitation amount in the user's unit. */
    val bars: List<RainWidgetBar> = emptyList()
)

@Serializable
data class RainWidgetBar(
    /** Pre-formatted time label (HH:mm). */
    val time: String,
    /** Precipitation amount for this bar in the user's precipitation unit. */
    val amount: Double,
    /** Per-bar intensity color as ARGB int (nowcast mm/h ramp or hourly mm ramp). */
    val colorArgb: Int
)