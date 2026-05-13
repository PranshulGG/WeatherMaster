package com.pranshulgg.weathermaster.core.utils.formatters

import java.util.Locale


fun formatNumbers(locale: Locale = Locale.US, number: Double, decimalPlaces: Int = 0): String {
    return "%,.${decimalPlaces}f".format(locale, number)
}
