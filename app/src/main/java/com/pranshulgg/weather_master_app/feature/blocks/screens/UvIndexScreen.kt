package com.pranshulgg.weather_master_app.feature.blocks.screens

import android.content.Context
import android.util.Log
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
import androidx.core.graphics.toColor
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.weather.toName
import com.pranshulgg.weather_master_app.core.model.weather.uv.UvIndex
import com.pranshulgg.weather_master_app.core.model.weather.uv.getUvIndex
import com.pranshulgg.weather_master_app.core.model.weather.uv.toColor
import com.pranshulgg.weather_master_app.core.model.weather.uv.toLabel
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.components.AvatarIcon
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
import com.pranshulgg.weather_master_app.feature.blocks.components.MatBarChart
import com.pranshulgg.weather_master_app.feature.blocks.components.NoHourlyDataAvailable
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
    val context = LocalContext.current


    val fullDayHourly =
        findMatchingHourly(hourly, weather.daily[index].time, weather.location.source)

    val maxUv = fullDayHourly.maxOf { it.uvIndex!! }
    val minUv = fullDayHourly.minOf { it.uvIndex!! }
    val uvIndexes = UvIndex.entries
    val date = toDateString(weather.daily[index].time, weather.location.timezone)
    val uvIndexData = fullDayHourly.map { it.uvIndex }


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
            if (!uvIndexData.contains(null)) {
                BarChart(
                    max = maxUv,
                    min = minUv,
                    times = fullDayHourly.map { it.time },
                    values = fullDayHourly.map { it.uvIndex?.roundToInt() ?: 0 },
                    context
                )
            } else {
                NoHourlyDataAvailable()
            }
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
                                getUvIndexScaleFor(it),
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

private fun getUvIndexScaleFor(index: UvIndex): String {
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
    max: Double,
    min: Double,
    times: List<Long>,
    values: List<Int>,
    context: Context
) {


    val is24hr = LocalAppPrefs.current.is24HrTimeFormat

    val bottomValues = times.slice(6..times.lastIndex step 6)

    val barHeights = values.map {
        val percentage = ((it.minus(min)).div((max - min))).times(100)
        max((percentage.div(100)).times(160).roundToInt(), 5)
    }

    val sideValues = (0 until max.roundToInt()).sortedByDescending { it }.map { "$it" }

    val topValues = values.slice(6..values.lastIndex step 6)

    MatBarChart(
        topValues = topValues.map {
            {
                Text(
                    getUvIndex(it).toLabel(context),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        bottomValues = bottomValues.map {
            {

                val time = if (is24hr) to24HourTimeString(
                    it,
                    "UTC"
                ) else to12HourTimeString(it, "UTC")


                Text(time, style = MaterialTheme.typography.labelMedium)
            }
        },
        sideValues = sideValues,
        values = values,
        barHeights = barHeights,
        headerValue = "${max.roundToInt()}",
        headerSuffix = getUvIndex(max.roundToInt()).toLabel(context),
        barColor = values.map {
            getUvIndex(it).toColor()
        },
        headerTitle = stringResource(R.string.weather_max_for_the_day),
        chartHeight = 250.dp
    )
}

