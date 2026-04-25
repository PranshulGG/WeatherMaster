package com.pranshulgg.weathermaster.feature.main.ui

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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.model.TemperatureUnits
import com.pranshulgg.weathermaster.core.model.domain.AppWeatherUnits
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.model.toIcon
import com.pranshulgg.weathermaster.core.model.toLabel
import com.pranshulgg.weathermaster.core.ui.components.Gap
import com.pranshulgg.weathermaster.core.ui.components.Symbol
import com.pranshulgg.weathermaster.core.ui.components.WeatherIconBox
import com.pranshulgg.weathermaster.core.utils.UnitConverter
import com.pranshulgg.weathermaster.core.utils.WeatherUtils.getLastUpdatedTimeString
import java.time.Instant
import kotlin.math.roundToInt

@Composable
fun CurrentWeatherCard(
    weather: Weather,
    units: AppWeatherUnits
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
            CardRowContent(weather, units)
        }
        MinMaxTempRow(weather, units)
    }

}

@Composable
private fun CardRowContent(weather: Weather, units: AppWeatherUnits) {

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
        Text("Now", color = colorScheme.secondary, fontWeight = FontWeight.Medium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "${currentTemp.roundToInt()}°",
                color = colorScheme.primary,
                fontSize = 62.sp,
                fontWeight = FontWeight.Medium
            )
            WeatherIconBox(
                current.weatherCondition.toIcon(
                    targetTimeSecs = weather.current.time,
                    daily = weather.daily.firstOrNull()
                ),
                size = 42.dp
            )
        }
    }
    Column(horizontalAlignment = Alignment.End) {
        Text(
            current.weatherCondition.toLabel(),
            color = colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            "Feels like: ${feelsLike.roundToInt()}°",
            color = colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


@Composable
private fun MinMaxTempRow(weather: Weather, units: AppWeatherUnits) {
    val colorScheme = MaterialTheme.colorScheme
    val daily = weather.daily[0]

    val minTemp =
        UnitConverter.convertTemp(daily.temperatureMin, TemperatureUnits.CELSIUS, units.tempUnit)
    val maxTemp =
        UnitConverter.convertTemp(daily.temperatureMax, TemperatureUnits.CELSIUS, units.tempUnit)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Max: ${minTemp.roundToInt()}° Min: ${maxTemp.roundToInt()}°",
            color = colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge
        )
        LastUpdatedTextRow(weather.current.lastUpdatedSecs)
    }
}


@Composable
private fun LastUpdatedTextRow(timeSeconds: Long) {
    val lastUpdated = getLastUpdatedTimeString(timeSeconds)

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