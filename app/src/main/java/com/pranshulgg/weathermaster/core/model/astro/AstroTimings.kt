package com.pranshulgg.weathermaster.core.model.astro

data class SunTimings(
    val time: Long,
    val sunrise: Long?,
    val sunset: Long?
)

data class MoonTimings(
    val time: Long,
    val moonrise: Long?,
    val moonset: Long?,
    val phase: MoonPhase
)

enum class MoonPhase {
    NEW_MOON,
    WAXING_CRESCENT,
    FIRST_QUARTER,
    WAXING_GIBBOUS,
    FULL_MOON,
    WANING_GIBBOUS,
    LAST_QUARTER,
    WANING_CRESCENT
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