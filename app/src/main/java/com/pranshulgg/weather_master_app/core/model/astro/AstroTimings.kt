package com.pranshulgg.weather_master_app.core.model.astro

import com.pranshulgg.weather_master_app.R

data class SunTimings(
    val time: Long,
    val sunrise: Long?,
    val sunset: Long?,
    val dawn: Long?,
    val dusk: Long?
)

data class MoonTimings(
    val time: Long,
    val moonrise: Long?,
    val moonset: Long?,
    val phase: MoonPhase
)

enum class MoonPhase(val displayName: Int, val icon: Int) {
    NEW_MOON(R.string.moonphase_new_moon, R.drawable.moonphase_new_moon),
    WAXING_CRESCENT(R.string.moonphase_waxing_crescent, R.drawable.moonphase_waxing_crescent),
    FIRST_QUARTER(R.string.moonphase_first_quarter, R.drawable.moonphase_first_quarter),
    WAXING_GIBBOUS(R.string.moonphase_waxing_gibbous, R.drawable.moonphase_waxing_gibbous),
    FULL_MOON(R.string.moonphase_full_moon, R.drawable.moonphase_full_moon),
    WANING_GIBBOUS(R.string.moonphase_waning_gibbous, R.drawable.moonphase_waning_gibbous),
    LAST_QUARTER(
        R.string.moonphase_last_quarter,
        R.drawable.moonphase_third_quarter
    ), // TODO: FIX NAMING
    WANING_CRESCENT(R.string.moonphase_waning_crescent, R.drawable.moonphase_waning_crescent)
}

fun getMoonPhase(phase: Double): MoonPhase {
    return when {
        phase < -135 -> MoonPhase.NEW_MOON
        phase < -90 -> MoonPhase.WAXING_CRESCENT
        phase < -45 -> MoonPhase.FIRST_QUARTER
        phase < 0 -> MoonPhase.WAXING_GIBBOUS
        phase < 45 -> MoonPhase.FULL_MOON
        phase < 90 -> MoonPhase.WANING_GIBBOUS
        phase < 135 -> MoonPhase.LAST_QUARTER
        else -> MoonPhase.WANING_CRESCENT
    }
}