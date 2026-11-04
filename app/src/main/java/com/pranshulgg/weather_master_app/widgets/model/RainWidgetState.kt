package com.pranshulgg.weather_master_app.widgets.model

import kotlinx.serialization.Serializable

/**
 * Rain data for the homescreen rain widget. Sourced from the high-resolution
 * nowcast when available, else from the hourly model precipitation (the same
 * fallback the in-app "Highlight rain" option uses).
 *
 * For the nowcast the raw 5-minute slots are stored ([slots]); the widget
 * re-aggregates them at render time into a bar count that matches the widget
 * width (narrower widget -> coarser buckets, wider -> more ticks).
 */
@Serializable
data class RainWidgetState(
    /** Short unit label, e.g. "mm". */
    val unitLabel: String = "",
    /** Display name of the data source, e.g. "Buienradar". */
    val sourceLabel: String = "",
    /** True when the data comes from the radar nowcast (5-min steps). */
    val isNowcast: Boolean = false,
    /** Location timezone id, used to format slot time labels at render time. */
    val zoneId: String = "",
    /** 24-hour time preference at build time, used for slot labels. */
    val is24h: Boolean = true,
    /**
     * Pre-formatted hourly bars for the no-nowcast fallback (oldest first).
     * The widget shows the first k bars, where k depends on widget width.
     */
    val bars: List<RainWidgetBar> = emptyList(),
    /**
     * Raw nowcast slots: epoch start millis of the slot and the expected
     * precipitation in mm accumulated over the slot. Slot length is
     * [slotMinutes] (5 = radar nowcast, 15 = minutely-15 fallback).
     */
    val slots: List<RainWidgetSlot> = emptyList(),
    /** Native slot length of [slots] in minutes (5 or 15). */
    val slotMinutes: Int = 5,
    /** Factor converting mm into the user's precipitation unit (for axis labels). */
    val mmToUnitFactor: Double = 1.0,
    /** Attribution line, e.g. "Radar © KNMI". Empty = no attribution shown. */
    val attribution: String = ""
) {
    /** True when there is anything renderable. */
    fun hasData(): Boolean = slots.isNotEmpty() || bars.isNotEmpty()
}

/**
 * One pre-formatted fallback bar: time label (HH:mm), amount already in the
 * user's precipitation unit, and the bar color as ARGB int.
 */
@Serializable
data class RainWidgetBar(
    /** Pre-formatted time label (HH:mm). */
    val time: String,
    /** Precipitation amount for this bar in the user's precipitation unit. */
    val amount: Double,
    /** Per-bar intensity color as ARGB int (nowcast mm/h ramp or hourly mm ramp). */
    val colorArgb: Int
)

@Serializable
data class RainWidgetSlot(
    /** Epoch millis of the slot start, aligned to 5-minute boundaries. */
    val time: Long,
    /** Expected precipitation in mm accumulated over the 5-minute slot. */
    val mm: Double
)