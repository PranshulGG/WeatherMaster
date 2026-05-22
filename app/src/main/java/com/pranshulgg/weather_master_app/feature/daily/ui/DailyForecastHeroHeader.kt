package com.pranshulgg.weather_master_app.feature.daily.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherDaily
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnits
import com.pranshulgg.weather_master_app.core.model.weather.toIcon
import com.pranshulgg.weather_master_app.core.model.weather.toLabel
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.WeatherIconBox
import com.pranshulgg.weather_master_app.core.utils.formatters.toDateString
import com.pranshulgg.weather_master_app.core.utils.weather.UnitConverter
import kotlin.math.roundToInt


@Composable
fun DailyForecastHeroHeader(
    daily: WeatherDaily,
    location: Location,
    units: WeatherUnits
) {

    val date = toDateString(daily.time, location.timezone)
    val context = LocalContext.current

    val maxTemp = UnitConverter.convertTemp(
        daily.temperatureMax,
        TemperatureUnits.CELSIUS,
        units.tempUnit
    )?.roundToInt() ?: "-"

    val minTemp = UnitConverter.convertTemp(
        daily.temperatureMin,
        TemperatureUnits.CELSIUS,
        units.tempUnit
    )?.roundToInt() ?: "-"

    Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp)) {
        Text(
            date,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            location.name,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Gap(5.dp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "${maxTemp}°",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.W900
            )
            Gap(horizontal = 6.dp)
            Text(
                "${minTemp}°",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.W900
            )
            Gap(horizontal = 12.dp)
            WeatherIconBox(
                daily.weatherCondition.toIcon(targetTimeMilli = System.currentTimeMillis()),
                size = 54.dp
            )
        }
        Text(
            daily.weatherCondition.toLabel(context),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }

}