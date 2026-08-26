package com.pranshulgg.weather_master_app.core.utils.weather.astronomy

import com.pranshulgg.weather_master_app.core.model.astro.MoonTimings
import com.pranshulgg.weather_master_app.core.model.astro.SunTimings
import com.pranshulgg.weather_master_app.core.model.astro.getMoonPhase
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.secondsToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.formatters.safeZoneId
import org.shredzone.commons.suncalc.MoonIllumination
import org.shredzone.commons.suncalc.MoonTimes
import org.shredzone.commons.suncalc.SunTimes
import java.time.Instant
import java.time.ZoneId

fun getSunTimings(
    timeMilli: List<Long>,
    zoneId: String,
    lat: Double,
    lon: Double
): List<SunTimings> {

    return timeMilli.map {
        val date = Instant.ofEpochMilli(it)
            .atZone(safeZoneId(zoneId))
            .toLocalDate()


        val sunTimes = SunTimes.compute()
            .on(date)
            .fullCycle()
            .timezone(safeZoneId(zoneId))
            .at(lat, lon)
            .execute()

        val civilTwilight = SunTimes.compute()
            .on(date)
            .timezone(safeZoneId(zoneId))
            .at(lat, lon)
            .twilight(SunTimes.Twilight.CIVIL)
            .execute()

        val dawn = civilTwilight.rise
        val dusk = civilTwilight.set

        SunTimings(
            it,
            sunTimes.rise?.toEpochSecond()?.secondsToMilliseconds(),
            sunTimes.set?.toEpochSecond()?.secondsToMilliseconds(),
            dawn?.toEpochSecond()?.secondsToMilliseconds(),
            dusk?.toEpochSecond()?.secondsToMilliseconds()
        )

    }

}

fun getMoonTimings(
    timeMilli: List<Long>,
    zoneId: String,
    lat: Double,
    lon: Double
): List<MoonTimings> {

    return timeMilli.map {
        val date = Instant.ofEpochMilli(it)
            .atZone(safeZoneId(zoneId))
            .toLocalDate()


        val moonTimes = MoonTimes.compute()
            .on(date)
            .at(lat, lon)
            .timezone(safeZoneId(zoneId))
            .execute()

        val phase = MoonIllumination.compute().on(date).execute().phase

        val phaseName = getMoonPhase(phase)


        /**
         * Moonrise/moonset pairing fix by https://github.com/reveler-hub
         *
         * Since the moon doesn't follow a clean 24 hr cycle, the lib can return a rise and set that don't belong
         * to the same cycle
         *
         * Like for e.g. rise = Aug 23, 13:55, set = Aug 23, 01:56
         * 01:56 occurs before 13:55, that set belongs to the previous day. We keep the Aug 23 rise
         * and use the next day's set instead
         */
        val resolveSet =
            if (moonTimes.rise != null && moonTimes.set != null && moonTimes.rise!! > moonTimes.set) {
                MoonTimes.compute()
                    .on(date.plusDays(1))
                    .at(lat, lon)
                    .timezone(safeZoneId(zoneId))
                    .execute().set
            } else {
                moonTimes.set
            }

        MoonTimings(
            it,
            moonTimes.rise?.toEpochSecond()?.secondsToMilliseconds(),
            resolveSet?.toEpochSecond()?.secondsToMilliseconds(),
            phase = phaseName
        )
    }
}
