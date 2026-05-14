package com.pranshulgg.weathermaster.core.utils.weather.astronomy

import com.pranshulgg.weathermaster.core.model.astro.MoonTimings
import com.pranshulgg.weathermaster.core.model.astro.SunTimings
import com.pranshulgg.weathermaster.core.model.astro.getMoonPhase
import com.pranshulgg.weathermaster.core.utils.formatters.toMilliseconds
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
            .atZone(ZoneId.of(zoneId))
            .toLocalDate()


        val sunTimes = SunTimes.compute()
            .on(date)
            .fullCycle()
            .timezone(zoneId)
            .at(lat, lon)
            .execute()

        SunTimings(
            it,
            sunTimes.rise?.toEpochSecond()?.toMilliseconds(),
            sunTimes.set?.toEpochSecond()?.toMilliseconds()
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
            .atZone(ZoneId.of(zoneId))
            .toLocalDate()


        val moonTimes = MoonTimes.compute()
            .on(date)
            .at(lat, lon)
            .timezone(zoneId)
            .execute()

        val phase = MoonIllumination.compute().on(date).execute().phase

        val phaseName = getMoonPhase(phase)


        MoonTimings(
            it,
            moonTimes.rise?.toEpochSecond()?.toMilliseconds(),
            moonTimes.set?.toEpochSecond()?.toMilliseconds(),
            phase = phaseName
        )
    }
}
