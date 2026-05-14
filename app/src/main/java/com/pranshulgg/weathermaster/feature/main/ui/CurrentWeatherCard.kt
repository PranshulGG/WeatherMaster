package com.pranshulgg.weathermaster.feature.main.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.model.weather.TemperatureUnits
import com.pranshulgg.weathermaster.core.model.domain.AppWeatherUnits
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.model.weather.toIcon
import com.pranshulgg.weathermaster.core.model.weather.toLabel
import com.pranshulgg.weathermaster.core.ui.components.Gap
import com.pranshulgg.weathermaster.core.ui.components.Symbol
import com.pranshulgg.weathermaster.core.ui.components.WeatherIconBox
import com.pranshulgg.weathermaster.core.utils.formatters.getLastUpdatedTimeString
import com.pranshulgg.weathermaster.core.utils.weather.UnitConverter
import kotlin.math.roundToInt

@Composable
fun CurrentWeatherCard(
    weather: Weather,
    units: AppWeatherUnits,
    context: Context
) {

    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CardRowContent(weather, units, context)
        }
        MinMaxTempRow(weather, units, context)
    }

}

@Composable
private fun CardRowContent(weather: Weather, units: AppWeatherUnits, context: Context) {

    val colorScheme = MaterialTheme.colorScheme
    val current = weather.current

    val currentTemp =
        UnitConverter.convertTemp(current.temperature, TemperatureUnits.CELSIUS, units.tempUnit)

    val feelsLike = UnitConverter.convertTemp(
        current.feelsLike ?: 0.0,
        TemperatureUnits.CELSIUS,
        units.tempUnit
    )


    Column {
        Text(
            stringResource(R.string.time_now),
            color = colorScheme.secondary,
            fontWeight = FontWeight.Medium
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "${currentTemp.roundToInt()}°",
                color = colorScheme.primary,
                fontSize = 62.sp,
                fontWeight = FontWeight.Medium
            )
            WeatherIconBox(
                current.weatherCondition.toIcon(
                    targetTimeMilli = weather.current.time,
                    daily = weather.daily.firstOrNull()
                ),
                size = 42.dp
            )
        }
    }
    Column(horizontalAlignment = Alignment.End) {
        Text(
            current.weatherCondition.toLabel(context),
            color = colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            stringResource(R.string.temp_feels_like, "${feelsLike.roundToInt()}°"),
            color = colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


@Composable
private fun MinMaxTempRow(weather: Weather, units: AppWeatherUnits, context: Context) {


    val colorScheme = MaterialTheme.colorScheme
    val daily = weather.daily.getOrNull(0)

    val minTemp = UnitConverter.convertTemp(
        daily?.temperatureMin ?: -99999.0,
        TemperatureUnits.CELSIUS,
        units.tempUnit
    )
    val maxTemp = UnitConverter.convertTemp(
        daily?.temperatureMax ?: -99999.0,
        TemperatureUnits.CELSIUS,
        units.tempUnit
    )


    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "${
                stringResource(
                    R.string.temp_max,
                    "${if (maxTemp == -99999.0) "N/A" else maxTemp.roundToInt()}°"
                )
            } ${
                stringResource(
                    R.string.temp_min,
                    "${if (minTemp == -99999.0) "N/A" else minTemp.roundToInt()}°"
                )
            }",
            color = colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge
        )
        LastUpdatedTextRow(weather.current.lastUpdatedInMilli, context)
    }
}


@Composable
private fun LastUpdatedTextRow(timeMilli: Long, context: Context) {
    val lastUpdated = getLastUpdatedTimeString(context, timeMilli)

    Row(verticalAlignment = Alignment.CenterVertically) {
        Symbol(
            R.drawable.schedule_48px,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            size = 14.dp
        )
        Gap(horizontal = 4.dp)
        Text(
            lastUpdated,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
            fontStyle = FontStyle.Italic
        )
    }
}