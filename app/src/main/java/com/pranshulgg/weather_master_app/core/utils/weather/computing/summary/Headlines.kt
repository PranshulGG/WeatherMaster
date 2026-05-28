package com.pranshulgg.weather_master_app.core.utils.weather.computing.summary

import android.content.Context
import androidx.compose.ui.res.stringResource
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit
import com.pranshulgg.weather_master_app.core.prefs.AppPrefsState
import com.pranshulgg.weather_master_app.core.prefs.helper.PreferencesHelper
import com.pranshulgg.weather_master_app.core.utils.formatters.to12HourTimeString
import com.pranshulgg.weather_master_app.core.utils.formatters.to24HourTimeString
import kotlin.math.roundToInt


// Pretty basic for now
fun getHeadline(
    summaryData: SummaryData,
    zoneId: String,
    units: WeatherUnits,
    context: Context,
): String {

    val rain = summaryData.rain
    val snow = summaryData.snow
    val peakUv = summaryData.uv
    val is24hr = PreferencesHelper.getBool("is24HrTimeFormat") ?: true

    val peakRainyAt = if (is24hr) to24HourTimeString(rain.at, zoneId) else to12HourTimeString(
        rain.at,
        zoneId
    )

    val peakSnowAt = if (is24hr) to24HourTimeString(snow.at, zoneId) else to12HourTimeString(
        snow.at,
        zoneId
    )

    val peakUvAt = if (is24hr) to24HourTimeString(peakUv.at, zoneId) else to12HourTimeString(
        peakUv.at,
        zoneId
    )

    val parts = mutableListOf<String>()


    val overviewTemplates = listOf(
        context.getString(R.string.summary_overview_template_1, summaryData.condition),
        context.getString(R.string.summary_overview_template_2, summaryData.condition),
        context.getString(R.string.summary_overview_template_3, summaryData.condition),
        context.getString(R.string.summary_overview_template_4, summaryData.condition)
    )

    parts += overviewTemplates.random()


    val rainSentence = when {
        rain.amount == 0.0 -> null

        rain.probability >= 80 ->
            listOf(
                context.getString(R.string.summary_rain_high_template_1, peakRainyAt),
                context.getString(R.string.summary_rain_high_template_2, peakRainyAt),
                context.getString(R.string.summary_rain_high_template_3, peakRainyAt),
                context.getString(R.string.summary_rain_high_template_4, peakRainyAt),
            ).random()

        rain.probability >= 40 ->
            listOf(
                context.getString(R.string.summary_rain_medium_template_1, peakRainyAt),
                context.getString(R.string.summary_rain_medium_template_2, peakRainyAt),
                context.getString(R.string.summary_rain_medium_template_3, peakRainyAt),
                context.getString(R.string.summary_rain_medium_template_4, peakRainyAt),
            ).random()

        else ->
            listOf(
                context.getString(R.string.summary_rain_low_template_1, peakRainyAt),
                context.getString(R.string.summary_rain_low_template_2, peakRainyAt),
                context.getString(R.string.summary_rain_low_template_3, peakRainyAt),
            ).random()
    }

    rainSentence?.let { parts += it }


    val snowSentence = when {
        snow.amount == 0.0 -> null

        snow.probability >= 80 ->
            listOf(
                context.getString(R.string.summary_snow_high_template_1, peakSnowAt),
                context.getString(R.string.summary_snow_high_template_2, peakSnowAt),
                context.getString(R.string.summary_snow_high_template_3, peakSnowAt),
                context.getString(R.string.summary_snow_high_template_4)
            ).random()

        snow.probability >= 40 ->
            listOf(
                context.getString(R.string.summary_snow_medium_template_1, peakSnowAt),
                context.getString(R.string.summary_snow_medium_template_2, peakSnowAt),
                context.getString(R.string.summary_snow_medium_template_3, peakSnowAt),
                context.getString(R.string.summary_snow_medium_template_4)
            ).random()

        else ->
            listOf(
                context.getString(R.string.summary_snow_low_template_1, peakSnowAt),
                context.getString(R.string.summary_snow_low_template_2, peakSnowAt),
                context.getString(R.string.summary_snow_low_template_3)
            ).random()
    }

    snowSentence?.let { parts += it }

    val uvSentence = when {
        peakUv.uv >= 10 ->
            listOf(
                context.getString(R.string.summary_uv_extreme_template_1, peakUvAt),
                context.getString(R.string.summary_uv_extreme_template_2, peakUvAt),
                context.getString(R.string.summary_uv_extreme_template_3, peakUvAt)
            ).random()

        peakUv.uv >= 7 ->
            listOf(
                context.getString(R.string.summary_uv_high_template_1, peakUvAt),
                context.getString(R.string.summary_uv_high_template_2, peakUvAt),
                context.getString(R.string.summary_uv_high_template_3, peakUvAt)
            ).random()

        peakUv.uv >= 4 ->
            listOf(
                context.getString(R.string.summary_uv_moderate_template_1),
                context.getString(R.string.summary_uv_moderate_template_2),
                context.getString(R.string.summary_uv_moderate_template_3)
            ).random()

        else -> null
    }

    uvSentence?.let { parts += it }


    val tempAvg =
        TemperatureUnit.CELSIUS.convert(summaryData.temps.avg, units.tempUnit)?.roundToInt()!!
    val tempMin =
        TemperatureUnit.CELSIUS.convert(summaryData.temps.min, units.tempUnit)?.roundToInt()!!

    val tempAvgSentence = when {


        tempAvg >= 35 -> listOf(
            context.getString(R.string.summary_temp_hot_template_1, "${tempAvg}°"),
            context.getString(R.string.summary_temp_hot_template_2),
            context.getString(R.string.summary_temp_hot_template_3, "${tempMin}°")
        ).random()

        tempAvg >= 25 -> listOf(
            context.getString(R.string.summary_temp_warm_template_1, "${tempAvg}°"),
            context.getString(R.string.summary_temp_warm_template_2),
            context.getString(R.string.summary_temp_warm_template_3, "${tempAvg}°")
        ).random()

        tempAvg >= 15 -> listOf(
            context.getString(R.string.summary_temp_mild_template_1),
            context.getString(R.string.summary_temp_mild_template_2, "${tempAvg}°"),
            context.getString(R.string.summary_temp_mild_template_3)
        ).random()

        tempAvg >= 5 -> listOf(
            context.getString(R.string.summary_temp_cool_template_1, "${tempAvg}°"),
            context.getString(R.string.summary_temp_cool_template_2),
            context.getString(R.string.summary_temp_cool_template_3)
        ).random()

        else -> listOf(
            context.getString(R.string.summary_temp_cold_template_1),
            context.getString(R.string.summary_temp_cold_template_2),
            context.getString(R.string.summary_temp_cold_template_3, "${tempMin}°")
        ).random()
    }


    parts += tempAvgSentence


    return parts.joinToString(" ")
}
