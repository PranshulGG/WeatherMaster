package com.pranshulgg.weathermaster.feature.shared.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.model.TemperatureUnits
import com.pranshulgg.weathermaster.core.model.WeatherConditions
import com.pranshulgg.weathermaster.core.model.domain.AppWeatherUnits
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.model.toIcon
import com.pranshulgg.weathermaster.core.ui.components.Symbol
import com.pranshulgg.weathermaster.core.ui.components.WeatherIconBox
import com.pranshulgg.weathermaster.core.utils.TimeFormatters
import com.pranshulgg.weathermaster.core.utils.UnitConverter
import com.pranshulgg.weathermaster.core.utils.WeatherUtils
import kotlin.math.roundToInt

@Composable
fun HourlyCard(weather: Weather, units: AppWeatherUnits) {

    val lazyListState = rememberLazyListState()
    val filteredHourly =
        WeatherUtils().filterHourlyDataForDate(weather.hourly, weather.current.time)



    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    5.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                Symbol(
                    R.drawable.schedule_48px,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    "Hourly",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            LazyRow(state = lazyListState) {
                items(filteredHourly.size, key = { filteredHourly[it].time }) { index ->
                    val time = (filteredHourly[index].time * 1000L)
                    val item = filteredHourly[index]
                    val temperature = UnitConverter.convertTemp(
                        item.temperature,
                        TemperatureUnits.CELSIUS,
                        units.tempUnit
                    )

                    HourlyItem(
                        time = time,
                        precipitationProbability = item.precipitationProbability ?: 0,
                        condition = item.weatherCondition,
                        temperature = temperature
                    )

                }
            }
        }
    }

}

@Composable
private fun HourlyItem(
    time: Long,
    precipitationProbability: Int,
    condition: WeatherConditions,
    temperature: Double
) {

    Column() {
        Text("${temperature.roundToInt()}°")
        WeatherIconBox(condition.toIcon(true), size = 38.dp)
        if (precipitationProbability > 0) {
            Text(
                "${precipitationProbability}%",
                color = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            TimeFormatters().to12HourTimeString(time),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}