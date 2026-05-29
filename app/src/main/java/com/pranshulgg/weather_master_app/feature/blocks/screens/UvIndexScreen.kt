package com.pranshulgg.weather_master_app.feature.blocks.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.weather.uv.UvIndex
import com.pranshulgg.weather_master_app.core.model.weather.uv.getUvIndex
import com.pranshulgg.weather_master_app.core.model.weather.uv.toColor
import com.pranshulgg.weather_master_app.core.model.weather.uv.toLabel
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.components.AvatarIcon
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.components.NavigateUpBtn
import com.pranshulgg.weather_master_app.core.ui.theme.ShadowElevation
import com.pranshulgg.weather_master_app.core.utils.formatters.to12HourTimeString
import com.pranshulgg.weather_master_app.core.utils.formatters.to24HourTimeString
import com.pranshulgg.weather_master_app.core.utils.formatters.toDateString
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findMatchingHourly
import com.pranshulgg.weather_master_app.feature.blocks.BlocksScreenViewModel
import com.pranshulgg.weather_master_app.feature.blocks.components.AboutCard
import com.pranshulgg.weather_master_app.feature.blocks.components.AboutCardText
import com.pranshulgg.weather_master_app.feature.blocks.components.ChartBarItem
import com.pranshulgg.weather_master_app.feature.blocks.components.ScaleCard
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun UvIndexScreen(navController: NavController, index: Int = 0, locationId: String) {

    val viewModel: BlocksScreenViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.getUnitsOnce()
        viewModel.getWeather(locationId)
    }


    val uiState = viewModel.uiState.value
    val weather = uiState.weather
    val hourly = weather?.hourly ?: return
    val currentMilli = if (index == 0) weather.current.time else weather.daily[index].time
    val context = LocalContext.current


    val data = findMatchingHourly(hourly, currentMilli, weather.location.source)
    val fullDayHourly =
        findMatchingHourly(hourly, weather.daily[index].time, weather.location.source)

    val maxUv = fullDayHourly.maxOf { it.uvIndex!! }
    val minUv = fullDayHourly.minOf { it.uvIndex!! }
    val uvIndexes = UvIndex.entries
    val date = toDateString(weather.daily[index].time, weather.location.timezone)


    LargeTopBarScaffold(
        title = stringResource(R.string.weather_uv_index),
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
                maxSuffix = getUvIndex(weather.daily[index].uvIndexMax?.roundToInt() ?: 0).toLabel(
                    context
                ),
                max = maxUv,
                min = minUv,
                times = data.map { it.time },
                values = data.map { it.uvIndex?.roundToInt() ?: 0 },
                weather.location.timezone
            )
            Gap(14.dp)
            AboutCard {
                AboutCardText(stringResource(R.string.weather_about_uv_index))
            }
            Gap(14.dp)
            ScaleCard {
                uvIndexes.forEach {
                    ListItem(
                        modifier = Modifier.height(45.dp),
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        leadingContent = {
                            AvatarIcon(
                                R.drawable.wb_sunny_24px,
                                containerColor = it.toColor(),
                                contentColor = Color.White
                            )
                        },
                        headlineContent = { Text(it.toLabel(context)) },
                        trailingContent = {
                            Text(
                                getUvIndexValuesFor(it),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    )
                }
            }
            Gap(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 30.dp)

        }
    }
}

fun getUvIndexValuesFor(index: UvIndex): String {
    return when (index) {
        UvIndex.LOW -> "1 - 2"
        UvIndex.MODERATE -> "3 - 5"
        UvIndex.HIGH -> "6 - 7"
        UvIndex.VERY_HIGH -> "8 - 10"
        UvIndex.EXTREME -> "11+"
    }
}

@Composable
private fun BarChart(
    maxSuffix: String,
    max: Double,
    min: Double,
    times: List<Long>,
    values: List<Int>,
    zoneId: String,
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
            Header(max.roundToInt().toString(), maxSuffix)

            LazyRow(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .height(230.dp)
                    .padding(bottom = 16.dp)
            ) {
                items(times.size, key = { times[it] }) { index ->

                    val item = values[index]

                    val valuePercentage = ((item.minus(min)).div((max - min))).times(100)
                    val height = max((valuePercentage.div(100)).times(170).roundToInt(), 48)

                    val time = if (is24hr) to24HourTimeString(
                        times[index],
                        zoneId
                    ) else to12HourTimeString(times[index], zoneId)

                    if (index == 0) Gap(horizontal = 16.dp)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ChartBarItem(
                            valueComposable = {
                                Text(
                                    item.toString(),
                                    fontSize = 24.sp,
                                    color = getUvColor(item)
                                )
                            },
                            barBackgroundColor = getUvColor(item),
                            shapeColor = getShapeColor(item),
                            height = height
                        )
                        Gap(5.dp)
                        Text(
                            time,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
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
private fun getUvColor(value: Int): Color {
    return when {
        value <= 3 -> MaterialTheme.colorScheme.primaryContainer
        value <= 7 -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.errorContainer
    }
}

@Composable
private fun getShapeColor(value: Int): Color {
    return when {
        value <= 3 -> MaterialTheme.colorScheme.onPrimaryContainer
        value <= 7 -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onErrorContainer
    }
}

@Composable
private fun Header(max: String, maxSuffix: String) {
    Column(
        modifier = Modifier.padding(top = 18.dp, start = 18.dp, end = 16.dp)
    ) {
        Text(
            stringResource(R.string.weather_max_for_the_day),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row() {
            Text(
                max,
                color = MaterialTheme.colorScheme.primaryContainer,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alignByBaseline(),

                )
            Gap(horizontal = 6.dp)
            Text(
                maxSuffix,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.alignByBaseline(),
            )

        }
    }
}