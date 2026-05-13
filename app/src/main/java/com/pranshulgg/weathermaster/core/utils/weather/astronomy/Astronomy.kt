package com.pranshulgg.weathermaster.core.utils.weather.astronomy

import com.pranshulgg.weathermaster.core.model.astro.MoonTimings
import com.pranshulgg.weathermaster.core.model.astro.SunTimings
import com.pranshulgg.weathermaster.core.model.astro.getMoonPhase
import org.shredzone.commons.suncalc.MoonIllumination
import org.shredzone.commons.suncalc.MoonTimes
import org.shredzone.commons.suncalc.SunTimes
import java.time.Instant
import java.time.ZoneId

fun getSunTimings(
    timeSecs: List<Long>,
    zoneId: String,
    lat: Double,
    lon: Double
): List<SunTimings> {

    return timeSecs.map {
        val date = Instant.ofEpochSecond(it)
            .atZone(ZoneId.of(zoneId))
            .toLocalDate()


        val sunTimes = SunTimes.compute()
            .on(date)
            .fullCycle()
            .timezone(zoneId)
            .at(lat, lon)
            .execute()

        SunTimings(it, sunTimes.rise?.toEpochSecond(), sunTimes.set?.toEpochSecond())

    }

}

fun getMoonTimings(
    timeSecs: List<Long>,
    zoneId: String,
    lat: Double,
    lon: Double
): List<MoonTimings> {

    return timeSecs.map {
        val date = Instant.ofEpochSecond(it)
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
            moonTimes.rise?.toEpochSecond(),
            moonTimes.set?.toEpochSecond(),
            phase = phaseName
        )
    }
}
