package com.pranshulgg.weathermaster.feature.main.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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
import com.pranshulgg.weathermaster.feature.main.components.MainSearchBar
import java.util.concurrent.TimeUnit
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun CurrentWeatherCard(
    paddingValues: PaddingValues,
    navController: NavController,
    drawerState: DrawerState,
    weather: Weather,
    units: AppWeatherUnits
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(236.dp)
            .padding(top = 8.dp)
    ) {

        MainSearchBar(
            isFroggyLayout = true,
            paddingValues = paddingValues,
            navController,
            drawerState,
            weather
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 16.dp, end = 16.dp)
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

    Column(Modifier.padding(top = 16.dp)) {
        Text("Now", color = colorScheme.secondary, fontWeight = FontWeight.Medium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "${currentTemp.roundToInt()}°",
                color = colorScheme.primary,
                fontSize = 62.sp,
                fontWeight = FontWeight.Medium
            )
            WeatherIconBox(current.weatherCondition.toIcon(true), size = 42.dp)
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
        LastUpdatedTextRow(weather.current.time)
    }
}


@Composable
private fun LastUpdatedTextRow(timeSeconds: Long) {

    val milli = timeSeconds * 1000L
    val ageMillis = System.currentTimeMillis() - milli
    val seconds = TimeUnit.MILLISECONDS.toSeconds(ageMillis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(ageMillis)
    val hours = TimeUnit.MILLISECONDS.toHours(ageMillis)
    val days = TimeUnit.MILLISECONDS.toDays(ageMillis)


    val lastUpdated = when {
        seconds < 60 -> "Just now"
        minutes < 60 -> "$minutes ${if (minutes == 1L) "min" else "mins"} ago"
        hours < 24 -> "$hours ${if (hours == 1L) "hr" else "hrs"} ago"
        else -> "$days ${if (days == 1L) "day" else "days"} ago"
    }

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