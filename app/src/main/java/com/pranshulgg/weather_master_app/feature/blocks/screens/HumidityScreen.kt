package com.pranshulgg.weather_master_app.feature.blocks.screens

import android.util.Log
import androidx.compose.animation.core.EaseInOutElastic
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.components.NavigateUpBtn
import com.pranshulgg.weather_master_app.core.ui.components.Symbol
import com.pranshulgg.weather_master_app.core.ui.theme.ShadowElevation
import com.pranshulgg.weather_master_app.core.utils.formatters.to12HourTimeString
import com.pranshulgg.weather_master_app.core.utils.formatters.to24HourTimeString
import com.pranshulgg.weather_master_app.core.utils.formatters.toDateString
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findMatchingHourly
import com.pranshulgg.weather_master_app.feature.blocks.BlocksScreenViewModel
import com.pranshulgg.weather_master_app.feature.blocks.components.AboutCard
import com.pranshulgg.weather_master_app.feature.blocks.components.AboutCardText
import com.pranshulgg.weather_master_app.feature.blocks.components.ChartBarItem
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun HumidityScreen(navController: NavController, index: Int = 0, locationId: String) {

    val viewModel: BlocksScreenViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.getUnitsOnce()
        viewModel.getWeather(locationId)
    }


    val uiState = viewModel.uiState.value
    val weather = uiState.weather
    val hourly = weather?.hourly ?: return
    val currentMilli = if (index == 0) weather.current.time else weather.daily[index].time
    val units = uiState.units


    val data = findMatchingHourly(hourly, currentMilli, weather.location.source)
    val fullDayHourly =
        findMatchingHourly(hourly, weather.daily[index].time, weather.location.source)

    val date = toDateString(weather.daily[index].time, weather.location.timezone)
    val avg = fullDayHourly.map { it.humidity ?: 0.0 }.average()

    val avgDewPoint =
        fullDayHourly.map { TemperatureUnit.CELSIUS.convert(it.dewPoint, units.tempUnit) ?: 0.0 }
            .average()

    val dewPointMax =
        fullDayHourly.maxOf { TemperatureUnit.CELSIUS.convert(it.dewPoint, units.tempUnit) ?: 0.0 }
    val dewPointMin =
        fullDayHourly.minOf { TemperatureUnit.CELSIUS.convert(it.dewPoint, units.tempUnit) ?: 0.0 }

    LargeTopBarScaffold(
        title = stringResource(R.string.weather_humidity),
        navigationIcon = { NavigateUpBtn(navController) },
        actions = {
            Text(
                date,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
        ) {
            BarChart(
                avg = avg.roundToInt(),
                times = data.map { it.time },
                values = data.map { it.humidity?.roundToInt() ?: 0 },
                weather.location.timezone,
            )
            Gap(14.dp)
            AboutCard {
                AboutCardText(stringResource(R.string.weather_about_humidity))
            }
            Gap(14.dp)
            BarChart(
                avg = avgDewPoint.roundToInt(),
                times = data.map { it.time },
                values = data.map { it.dewPoint?.roundToInt() ?: 0 },
                weather.location.timezone,
                isDewPoint = true,
                dewPointMax,
                dewPointMin
            )
            Gap(14.dp)
            AboutCard {
                AboutCardText(stringResource(R.string.weather_about_dewpoint))
            }
            Gap(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 30.dp)

        }
    }
}


@Composable
private fun BarChart(
    avg: Int,
    times: List<Long>,
    values: List<Int>,
    zoneId: String,
    isDewPoint: Boolean = false,
    dewPointMax: Double = 0.0,
    dewPointMin: Double = 0.0,
) {

    val prefs = LocalAppPrefs.current
    val is24hr = prefs.is24HrTimeFormat

    Surface(
        color = MaterialTheme.colorScheme.surfaceBright,
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = ShadowElevation.level2,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Header(
                if (!isDewPoint) "${avg}%" else "${avg}°",
                suffix = if (isDewPoint) stringResource(R.string.weather_dew_point) else null
            )

            LazyRow(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .height(if (isDewPoint) 240.dp else 230.dp)
                    .padding(bottom = 16.dp)
            ) {
                items(times.size, key = { times[it] }) { index ->

                    val item = values[index]

                    val percentage =
                        if (isDewPoint) ((item.minus(dewPointMin)).div((dewPointMax - dewPointMin))).times(
                            100
                        ).roundToInt() else item

                    val height = max((percentage.div(100f)).times(170), 48f)

                    val time = if (is24hr) to24HourTimeString(
                        times[index],
                        zoneId
                    ) else to12HourTimeString(times[index], zoneId)

                    if (index == 0) Gap(horizontal = 16.dp)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ChartBarItem(
                            valueComposable = {
                                Text(
                                    if (!isDewPoint) "$item" else "${item}°",
                                    fontSize = if (!isDewPoint) 20.sp else 18.sp,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                )
                            },
                            height = height.roundToInt()
                        )
                        Gap(5.dp)
                        Text(
                            time,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            textAlign = TextAlign.Center
                        )

                    }
                    if (index == times.size - 1) Gap(horizontal = 16.dp)
                }

            }
        }
    }
}


@Composable
private fun Header(avg: String, suffix: String?) {
    Column(
        modifier = Modifier.padding(top = 18.dp, start = 18.dp, end = 16.dp)
    ) {
        if (suffix != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    5.dp, alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Symbol(
                    R.drawable.dew_point_24px,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    suffix,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Gap(5.dp)

        }
        Text(
            stringResource(R.string.text_average_for_day),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row() {
            Text(
                avg,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alignByBaseline(),
            )

        }
    }
}