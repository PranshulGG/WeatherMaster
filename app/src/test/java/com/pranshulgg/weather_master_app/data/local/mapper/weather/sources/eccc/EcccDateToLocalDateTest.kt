package com.pranshulgg.weather_master_app.data.local.mapper.weather.sources.eccc

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class EcccDateToLocalDateTest {

    private fun parse(date: String, today: String) = ecccDateToLocalDate(date, LocalDate.parse(today))

    @Test
    fun sameYear() {
        assertEquals(LocalDate.parse("2026-10-13"), parse("Tue, 13 Oct", today = "2026-10-13"))
        assertEquals(LocalDate.parse("2026-10-19"), parse("Mon, 19 Oct", today = "2026-10-13"))
        assertEquals(LocalDate.parse("2026-10-05"), parse("Mon, 5 Oct", today = "2026-10-01"))
    }

    @Test
    fun forecastCrossingIntoNextYear() {
        assertEquals(LocalDate.parse("2027-01-01"), parse("Fri, 1 Jan", today = "2026-12-27"))
        assertEquals(LocalDate.parse("2027-01-02"), parse("Sat, 2 Jan", today = "2026-12-30"))
        assertEquals(LocalDate.parse("2026-12-31"), parse("Thu, 31 Dec", today = "2026-12-31"))
    }

    @Test
    fun previousYearAfterNewYear() {
        // a 31 Dec date read just after new year
        assertEquals(LocalDate.parse("2026-12-31"), parse("Thu, 31 Dec", today = "2027-01-01"))
    }

    @Test
    fun leapDay() {
        assertEquals(LocalDate.parse("2028-02-29"), parse("Tue, 29 Feb", today = "2028-02-27"))
    }
}
