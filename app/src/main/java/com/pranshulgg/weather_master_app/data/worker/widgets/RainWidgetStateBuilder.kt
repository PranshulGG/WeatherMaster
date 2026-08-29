package com.pranshulgg.weather_master_app.data.worker.widgets

import android.content.Context
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.weather.PrecipitationUnit
import com.pranshulgg.weather_master_app.core.model.weather.rain.RainNowcast
import com.pranshulgg.weather_master_app.core.model.weather.toName
import com.pranshulgg.weather_master_app.core.network.sources.weather.rain.RainNowcastRepository
import com.pranshulgg.weather_master_app.core.prefs.helper.PreferencesHelper
import com.pranshulgg.weather_master_app.core.utils.formatters.to12HourTimeString
import com.pranshulgg.weather_master_app.core.utils.formatters.to24HourTimeString
import com.pranshulgg.weather_master_app.widgets.model.RainWidgetBar
import com.pranshulgg.weather_master_app.widgets.model.RainWidgetState
import jakarta.inject.Inject

/**
 * Builds the state for the homescreen rain widget from the default location:
 * high-resolution nowcast bars when the location has coverage, otherwise the
 * hourly model precipitation bars (same fallback as the in-app card).
 */
class RainWidgetStateBuilder @Inject constructor(
    private val rainNowcastRepository: RainNowcastRepository
) {

    suspend fun build(
        context: Context,
        location: Location,
        weather: Weather,
        units: WeatherUnits
    ): RainWidgetState? {
        val unit = units.precipitationUnit
        val unitLabel = unit.toName(context, true)
        val is24h = PreferencesHelper.getBool("is24HrTimeFormat") ?: true

        fun formatTime(milli: Long): String =
            if (is24h) to24HourTimeString(milli, location.timezone)
            else to12HourTimeString(milli, location.timezone)

        fun formatAmount(valueInUnit: Double): Double {
            val decimals =
                if (unit == PrecipitationUnit.INCH) 3
                else if (valueInUnit < 1.0) 2
                else 1
            val factor = Math.pow(10.0, decimals.toDouble())
            return Math.round(valueInUnit * factor) / factor
        }

        // ---- primary: high-res nowcast -------------------------------------
        val nowcast = try {
            rainNowcastRepository.getNowcast(location)
        } catch (e: Exception) {
            null
        }

        if (nowcast != null && nowcast.points.isNotEmpty()) {
            return rainWidgetStateFromNowcast(nowcast, unit, unitLabel, ::formatTime, ::formatAmount)
        }

        // ---- fallback: hourly model precipitation --------------------------
        return rainWidgetStateFromHourly(location, weather, unit, unitLabel, ::formatTime, ::formatAmount)
    }
}

/** Rounds an amount already expressed in the user's precipitation unit. */
private fun formatAmountForMm(valueInUnit: Double): Double {
    val decimals = if (valueInUnit < 1.0) 2 else 1
    val factor = Math.pow(10.0, decimals.toDouble())
    return Math.round(valueInUnit * factor) / factor
}

internal fun rainWidgetStateFromNowcast(
    nowcast: RainNowcast,
    unit: PrecipitationUnit,
    unitLabel: String,
    formatTime: (Long) -> String,
    formatAmount: (Double) -> Double
): RainWidgetState {
    // One bar per 20 minutes -> 9 bars across the 3h window. Glance Rows
    // support at most 10 children, so the slot count must stay below that.
    val slotMinutes = 20L
    val slotMillis = slotMinutes * 60000L

    // Accumulate expected precipitation (mm) per 10-minute bucket. 5-min radar
    // points and 15-min model points both split cleanly into 5-min sub-steps.
    val buckets = linkedMapOf<Long, Double>()
    for (p in nowcast.points) {
        val end = p.time + p.intervalMinutes * 60000L
        var s = p.time
        while (s < end) {
            val bucket = s - (s % slotMillis)
            buckets[bucket] = (buckets[bucket] ?: 0.0) + p.intensityMmPerHour * 5.0 / 60.0
            s += 5 * 60000L
        }
    }

    val bars = buckets.entries.sortedBy { it.key }.map { (bucket, mmTotal) ->
        RainWidgetBar(
            time = formatTime(bucket),
            amount = formatAmount(unit.convert(mmTotal, unit) ?: mmTotal),
            colorArgb = nowcastBarColorArgb(mmTotal * 60.0 / slotMinutes).toInt()
        )
    }

    return RainWidgetState(
        unitLabel = unitLabel,
        sourceLabel = nowcast.providerName,
        isNowcast = true,
        bars = bars
    )
}

internal fun rainWidgetStateFromHourly(
    location: Location,
    weather: Weather,
    unit: PrecipitationUnit,
    unitLabel: String,
    formatTime: (Long) -> String,
    formatAmount: (Double) -> Double
): RainWidgetState {
    val nowMilli = System.currentTimeMillis()

    val start = weather.hourly
        .indexOfFirst { it.time >= nowMilli }
        .let { if (it == -1) 0 else maxOf(0, it - 1) }

    // One bar per hour, ~4 hours to visually match the nowcast density
    val bars = weather.hourly
        .drop(start)
        .take(4)
        .map { hour ->
            val mmInUnit = PrecipitationUnit.MM.convert(hour.rain, unit) ?: hour.rain
            RainWidgetBar(
                time = formatTime(hour.time),
                amount = formatAmount(mmInUnit),
                colorArgb = hourlyBarColorArgb(hour.rain).toInt()
            )
        }

    return RainWidgetState(
        unitLabel = unitLabel,
        sourceLabel = location.name,
        isNowcast = false,
        bars = bars
    )
}

/** buienradar-style mm/h intensity ramp, packed as ARGB longs. */
internal fun nowcastBarColorArgb(intensityMmPerHour: Double): Long = when {
    intensityMmPerHour <= 0.0 -> 0xFFBDBDBD
    intensityMmPerHour < 0.3 -> 0xFFA9D5F5
    intensityMmPerHour < 1.0 -> 0xFF6FB6EC
    intensityMmPerHour < 3.0 -> 0xFF3E8FE0
    intensityMmPerHour < 10.0 -> 0xFF2265C4
    intensityMmPerHour < 25.0 -> 0xFF1B3FA0
    else -> 0xFF6A1B9A
}

/** The hourly card's mm amount ramp, packed as ARGB longs. */
internal fun hourlyBarColorArgb(rainMm: Double): Long = when {
    rainMm <= 0.0 -> 0xFFE0E0E0
    rainMm < 1.0 -> 0xFFB3E5FC
    rainMm < 2.5 -> 0xFF4FC3F7
    rainMm < 7.5 -> 0xFF1E88E5
    rainMm < 20.0 -> 0xFF1565C0
    rainMm < 50.0 -> 0xFF6A1B9A
    else -> 0xFFD50000
}