package com.pranshulgg.weather_master_app.feature.main.ui

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
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit
import com.pranshulgg.weather_master_app.core.model.weather.toIcon
import com.pranshulgg.weather_master_app.core.model.weather.toLabel
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.Symbol
import com.pranshulgg.weather_master_app.core.ui.components.WeatherIconBox
import com.pranshulgg.weather_master_app.core.utils.formatters.getLastUpdatedTimeString
import kotlin.math.roundToInt

@Composable
fun CurrentWeatherCard(
    weather: Weather,
    units: WeatherUnits,
    context: Context,
    isFroggyLayout: Boolean = true
) {

    if (isFroggyLayout) {
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
    } else {
        PixelStyleCurrentWeatherCard(weather, units, context)
    }

}

@Composable
private fun CardRowContent(weather: Weather, units: WeatherUnits, context: Context) {

    val colorScheme = MaterialTheme.colorScheme
    val current = weather.current

    val currentTemp = TemperatureUnit.CELSIUS.convert(current.temperature, units.tempUnit)

    val feelsLike = TemperatureUnit.CELSIUS.convert(current.feelsLike, units.tempUnit)



    Column {
        Text(
            stringResource(R.string.time_now),
            color = colorScheme.secondary,
            fontWeight = FontWeight.Medium
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "${currentTemp?.roundToInt() ?: "-"}°",
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
            stringResource(R.string.temp_feels_like, "${feelsLike?.roundToInt() ?: "-"}°"),
            color = colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


@Composable
private fun MinMaxTempRow(
    weather: Weather,
    units: WeatherUnits,
    context: Context,
    isFroggyLayout: Boolean = true
) {


    val colorScheme = MaterialTheme.colorScheme
    val daily = weather.daily.getOrNull(0)


    val maxTemp = TemperatureUnit.CELSIUS.convert(daily?.temperatureMax, units.tempUnit)
    val minTemp = TemperatureUnit.CELSIUS.convert(daily?.temperatureMin, units.tempUnit)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isFroggyLayout) Arrangement.SpaceBetween else Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,

        ) {
        Text(
            "${
                stringResource(
                    R.string.temp_max,
                    "${maxTemp?.roundToInt() ?: "-"}°"
                )
            } ${
                stringResource(
                    R.string.temp_min,
                    "${minTemp?.roundToInt() ?: "-"}°"
                )
            }",
            color = colorScheme.onSurfaceVariant,
            style = if (isFroggyLayout) MaterialTheme.typography.labelLarge else MaterialTheme.typography.titleLarge
        )
        if (isFroggyLayout)
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

@Composable
fun PixelStyleCurrentWeatherCard(weather: Weather, units: WeatherUnits, context: Context) {
    val current = weather.current

    val currentTemp = TemperatureUnit.CELSIUS.convert(current.temperature, units.tempUnit)

    val feelsLike = TemperatureUnit.CELSIUS.convert(current.feelsLike, units.tempUnit)

    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 24.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            WeatherIconBox(
                current.weatherCondition.toIcon(
                    targetTimeMilli = weather.current.time,
                    daily = weather.daily.firstOrNull()
                ),
                size = 32.dp
            )
            Gap(horizontal = 6.dp)
            Text(
                current.weatherCondition.toLabel(context),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            "${currentTemp?.roundToInt() ?: "-"}°",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 136.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            stringResource(R.string.temp_feels_like, "${feelsLike?.roundToInt() ?: "-"}°"),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium
        )
        Gap(vertical = 6.dp)
        MinMaxTempRow(weather, units, context, false)
    }
}