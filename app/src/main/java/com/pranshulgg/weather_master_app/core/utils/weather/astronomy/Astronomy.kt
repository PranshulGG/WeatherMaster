package com.pranshulgg.weather_master_app.core.utils.weather.astronomy

import com.pranshulgg.weather_master_app.core.model.astro.MoonTimings
import com.pranshulgg.weather_master_app.core.model.astro.SunTimings
import com.pranshulgg.weather_master_app.core.model.astro.getMoonPhase
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.secondsToMilliseconds
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

        val civilTwilight = SunTimes.compute()
            .on(date)
            .timezone(zoneId)
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
            moonTimes.rise?.toEpochSecond()?.secondsToMilliseconds(),
            moonTimes.set?.toEpochSecond()?.secondsToMilliseconds(),
            phase = phaseName
        )
    }
}
