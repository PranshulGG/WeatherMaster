package com.pranshulgg.weather_master_app.feature.shared.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit
import com.pranshulgg.weather_master_app.core.model.weather.toIcon
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.WeatherIconBox
import com.pranshulgg.weather_master_app.core.ui.navigation.NavRoutes
import com.pranshulgg.weather_master_app.core.ui.theme.ShadowElevation
import com.pranshulgg.weather_master_app.core.utils.formatters.toWeekdayString
import com.pranshulgg.weather_master_app.core.utils.weather.cache.isWeatherDailyDomainSafe
import com.pranshulgg.weather_master_app.feature.shared.components.CardsHeader
import kotlin.math.roundToInt


@Composable
fun DailyCard(weather: Weather, units: WeatherUnits, navController: NavController) {

    if (!isWeatherDailyDomainSafe(weather)) return


    val daily = weather.daily

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
            CardsHeader(stringResource(R.string.weather_daily_forecast), R.drawable.date_range_24px)

            Gap(14.dp)

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(daily.size, key = { "${daily[it].time}_$it" }) { index ->

                    val item = daily[index]
                    val weekDay = toWeekdayString(
                        item.time,
                        weather.location.timezone
                    )

                    if (index == 0) Gap(horizontal = 16.dp)

                    DailyItem(
                        weekDay,
                        item.temperatureMax,
                        item.temperatureMin,
                        item.weatherCondition.toIcon(targetTimeMilli = System.currentTimeMillis()) /* always use day icons */,
                        item.precipitationProbabilityMax,
                        units,
                        onDailyItemClick = {
                            navController.navigate(
                                NavRoutes.daily(
                                    index,
                                    weather.location.id
                                )
                            )
                        }
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
    maxTemp: Double?,
    minTemp: Double?,
    icon: Int,
    precipitationProbability: Int?,
    units: WeatherUnits,
    onDailyItemClick: () -> Unit
) {


    val maxTemp = TemperatureUnit.CELSIUS.convert(maxTemp, units.tempUnit)?.roundToInt() ?: "-"
    val minTemp = TemperatureUnit.CELSIUS.convert(minTemp, units.tempUnit)?.roundToInt() ?: "-"

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = CircleShape,
        onClick = onDailyItemClick
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
                    modifier = Modifier.alpha(if (precipitationProbability == null) 0f else 1f),
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

