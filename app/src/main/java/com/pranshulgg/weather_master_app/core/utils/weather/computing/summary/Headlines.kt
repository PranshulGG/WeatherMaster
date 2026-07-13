package com.pranshulgg.weather_master_app.core.utils.weather.computing.summary

import android.content.Context
import android.util.Log
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


    val peakUv = summaryData.uv
    val is24hr = PreferencesHelper.getBool("is24HrTimeFormat") ?: true

    val formatter: (Long) -> String = {
        if (is24hr) {
            to24HourTimeString(it, zoneId)
        } else {
            to12HourTimeString(it, zoneId)
        }
    }


    val peakUvAt = formatter(peakUv.at)

    val parts = mutableListOf<String>()


    val overviewTemplates = if (summaryData.conditionDay == summaryData.conditionNight)
        context.getString(
            R.string.summary_overview_template_same_condition,
            summaryData.conditionDay.lowercase()
        ) else
        context.getString(
            R.string.summary_overview_template_day_night_condition,
            summaryData.conditionDay.lowercase(), summaryData.conditionNight.lowercase()
        )

    parts += overviewTemplates


    val rainSentence = when {
        summaryData.rainDay.amount == 0.0 && summaryData.rainNight.amount == 0.0 -> null

        summaryData.rainDay.probability >= 40 && summaryData.rainNight.probability >= 40 ->
            context.getString(
                R.string.summary_rain_template_day_night,
                formatter(summaryData.rainDay.at),
                "${summaryData.rainDay.probability}%",
                "${summaryData.rainNight.probability}%",
                formatter(summaryData.rainNight.at)
            )


        summaryData.rainDay.probability >= 40 && summaryData.rainNight.probability < 40 ->
            context.getString(
                R.string.summary_rain_template_day,
                formatter(summaryData.rainDay.at),
                "${summaryData.rainDay.probability}%",
            )


        summaryData.rainDay.probability < 40 && summaryData.rainNight.probability >= 40 ->
            context.getString(
                R.string.summary_rain_template_night,
                formatter(summaryData.rainNight.at),
                "${summaryData.rainNight.probability}%",
            )

        else -> null
    }

    rainSentence?.let { parts += it }


    val snowSentence = when {
        summaryData.snowDay.amount == 0.0 && summaryData.snowNight.amount == 0.0 -> null

        summaryData.snowDay.probability >= 40 && summaryData.snowNight.probability >= 40 ->
            context.getString(
                R.string.summary_snow_template_day_night,
                formatter(summaryData.snowDay.at),
                "${summaryData.snowDay.probability}%",
                "${summaryData.snowNight.probability}%",
                formatter(summaryData.snowNight.at)
            )

        summaryData.snowDay.probability >= 40 && summaryData.snowNight.probability < 40 ->
            context.getString(
                R.string.summary_snow_template_day,
                formatter(summaryData.snowDay.at),
                "${summaryData.snowDay.probability}%",
            )

        summaryData.snowDay.probability < 40 && summaryData.snowNight.probability >= 40 ->
            context.getString(
                R.string.summary_snow_template_night,
                formatter(summaryData.snowNight.at),
                "${summaryData.snowNight.probability}%",
            )

        else -> null
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


    val formatterTemperature: (Double) -> Int = {
        TemperatureUnit.CELSIUS.convert(it, units.tempUnit)?.roundToInt()!!
    }

    val tempMax = formatterTemperature(summaryData.temps.max)
    val tempMin = formatterTemperature(summaryData.temps.min)

    val tempSentence = when {
        tempMax >= 35 ->
            context.getString(R.string.summary_temp_template_extreme, "${tempMax}°", "${tempMin}°")

        tempMax >= 25 ->
            context.getString(R.string.summary_temp_template_warm, "${tempMax}°", "${tempMin}°")

        tempMax >= 15 ->
            context.getString(R.string.summary_temp_template_mild, "${tempMax}°", "${tempMin}°")

        tempMax >= 5 ->
            context.getString(R.string.summary_temp_template_cool, "${tempMax}°", "${tempMin}°")

        else ->
            context.getString(R.string.summary_temp_template_cold, "${tempMax}°", "${tempMin}°")
    }


    parts += tempSentence


    return parts.joinToString(" ")
}
