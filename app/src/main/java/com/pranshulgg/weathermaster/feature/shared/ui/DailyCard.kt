package com.pranshulgg.weathermaster.feature.shared.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.model.TemperatureUnits
import com.pranshulgg.weathermaster.core.model.domain.AppWeatherUnits
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.ui.components.Gap
import com.pranshulgg.weathermaster.core.ui.components.Symbol
import com.pranshulgg.weathermaster.core.ui.components.WeatherIconBox
import com.pranshulgg.weathermaster.core.utils.TimeFormatters
import com.pranshulgg.weathermaster.core.utils.UnitConverter
import com.pranshulgg.weathermaster.core.utils.WeatherUtils
import com.pranshulgg.weathermaster.feature.shared.components.CardsHeader
import java.time.ZoneId
import java.time.ZonedDateTime
import com.pranshulgg.weathermaster.core.model.toIcon
import com.pranshulgg.weathermaster.core.ui.theme.ShadowElevation


@Composable
fun DailyCard(weather: Weather, units: AppWeatherUnits) {

    val daily = weather.daily

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = ShadowElevation.level2
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
        ) {
            CardsHeader("Daily forecast", R.drawable.date_range_24px)

            Gap(14.dp)

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(daily.size, key = { daily[it].time }) { index ->

                    val item = daily[index]
                    val weekDay = TimeFormatters().toWeekdayString(
                        (item.time * 1000L),
                        weather.location.timezone
                    )

                    if (index == 0) Gap(horizontal = 16.dp)

                    DailyItem(
                        weekDay,
                        item.temperatureMax,
                        item.temperatureMin,
                        item.weatherCondition.toIcon(true),
                        item.precipitationProbabilityMax ?: 0,
                        units
                    )

                    if (index == daily.size - 1) Gap(horizontal = 16.dp)

                }
            }
        }
    }
}

@Composable
private fun DailyItem(
    weekday: String,
    maxTemp: Double,
    minTemp: Double,
    icon: Int,
    precipitationProbability: Int,
    units: AppWeatherUnits
) {
    val maxTemp = UnitConverter.convertTemp(
        maxTemp,
        TemperatureUnits.CELSIUS,
        units.tempUnit
    ).toInt()

    val minTemp = UnitConverter.convertTemp(
        minTemp,
        TemperatureUnits.CELSIUS,
        units.tempUnit
    ).toInt()


    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = CircleShape
    ) {
        Column(
            Modifier
                .height(210.dp)
                .width(65.dp)
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "${maxTemp}°",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "${minTemp}°",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WeatherIconBox(icon, size = 38.dp)
                Gap(8.dp)
                Text(
                    "${precipitationProbability}%",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Gap(4.dp)
                Text(
                    weekday,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}