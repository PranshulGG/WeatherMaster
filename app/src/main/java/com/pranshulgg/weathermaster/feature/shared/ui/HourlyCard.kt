package com.pranshulgg.weathermaster.feature.shared.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.model.TemperatureUnits
import com.pranshulgg.weathermaster.core.model.WeatherConditions
import com.pranshulgg.weathermaster.core.model.domain.AppWeatherUnits
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.model.toIcon
import com.pranshulgg.weathermaster.core.ui.components.Gap
import com.pranshulgg.weathermaster.core.ui.components.WeatherIconBox
import com.pranshulgg.weathermaster.core.ui.theme.ShadowElevation
import com.pranshulgg.weathermaster.core.utils.TimeFormatters
import com.pranshulgg.weathermaster.core.utils.UnitConverter
import com.pranshulgg.weathermaster.core.utils.WeatherUtils
import com.pranshulgg.weathermaster.feature.shared.components.CardsHeader
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.roundToInt

@Composable
fun HourlyCard(weather: Weather, units: AppWeatherUnits) {

    val lazyListState = rememberLazyListState()
    val filteredHourly =
        WeatherUtils.filterHourlyDataForDate(
            weather.hourly,
            ZonedDateTime.now(ZoneId.of(weather.location.timezone)).toEpochSecond()
        )


    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = ShadowElevation.level2
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
        ) {

            CardsHeader("Hourly forecast", R.drawable.schedule_48px)

            LazyRow(state = lazyListState) {
                items(filteredHourly.size, key = { filteredHourly[it].time }) { index ->
                    val time = TimeFormatters().to12HourTimeString(
                        (filteredHourly[index].time * 1000L),
                        weather.location.timezone
                    )
                    val item = filteredHourly[index]
                    val temperature = UnitConverter.convertTemp(
                        item.temperature,
                        TemperatureUnits.CELSIUS,
                        units.tempUnit
                    )

                    val matchingDaily = WeatherUtils.findMatchingDaily(
                        item.time,
                        weather.daily,
                        weather.location.timezone
                    )


                    if (index == 0) Gap(horizontal = 10.dp)

                    HourlyItem(
                        time = time,
                        precipitationProbability = item.precipitationProbability ?: 0,
                        temperature = temperature,
                        isNow = index == 0,
                        icon = item.weatherCondition.toIcon(matchingDaily, item.time)
                    )

                    if (index == filteredHourly.size - 1) Gap(horizontal = 10.dp)

                }
            }
        }
    }

}

@Composable
private fun HourlyItem(
    time: String,
    precipitationProbability: Int,
    temperature: Double,
    isNow: Boolean,
    icon: Int
) {

    Column(
        modifier = Modifier
            .height(120.dp)
            .width(45.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Gap(5.dp)
        TempWithShape(temperature, isNow)
        Gap(4.dp)
        Text(
            "${precipitationProbability}%",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier
                .padding(bottom = 4.dp)
                .alpha(if (precipitationProbability > 0) 1f else 0f)
        )
        WeatherIconBox(icon, size = 28.dp)
        Gap(3.dp)
        Text(
            time,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge
        )
    }
}


@Composable
private fun TempWithShape(temperature: Double, isNow: Boolean = false) {
    Surface(
        shape = MaterialShapes.Cookie4Sided.toShape(),
        modifier = Modifier
            .size(36.dp),
        color = if (isNow) MaterialTheme.colorScheme.primary else Color.Transparent
    ) {

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "${temperature.roundToInt()}°",
                color = if (isNow) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}