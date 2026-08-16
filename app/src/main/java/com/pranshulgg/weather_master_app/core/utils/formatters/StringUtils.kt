package com.pranshulgg.weather_master_app.core.utils.formatters

import java.util.Locale

// Geocoding/search providers are inconsistent about casing (e.g. "spain" instead of "Spain").
// Only the first letter of each word is touched, so already-correct casing (e.g. "USA") is untouched.
fun String.toTitleCase(): String =
    split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
