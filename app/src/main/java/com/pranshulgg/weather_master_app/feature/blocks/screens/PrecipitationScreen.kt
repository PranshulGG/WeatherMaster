package com.pranshulgg.weather_master_app.feature.blocks.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.weather.PrecipitationUnit
import com.pranshulgg.weather_master_app.core.model.weather.toName
import com.pranshulgg.weather_master_app.core.model.weather.uv.getUvIndex
import com.pranshulgg.weather_master_app.core.model.weather.uv.toLabel
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.components.NavigateUpBtn
import com.pranshulgg.weather_master_app.core.ui.components.Symbol
import com.pranshulgg.weather_master_app.core.utils.formatters.to12HourTimeString
import com.pranshulgg.weather_master_app.core.utils.formatters.to24HourTimeString
import com.pranshulgg.weather_master_app.core.utils.formatters.toDateString
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findMatchingHourly
import com.pranshulgg.weather_master_app.feature.blocks.BlocksScreenViewModel
import com.pranshulgg.weather_master_app.feature.blocks.components.AboutCard
import com.pranshulgg.weather_master_app.feature.blocks.components.AboutCardText
import com.pranshulgg.weather_master_app.feature.blocks.components.MatBarChart
import com.pranshulgg.weather_master_app.feature.blocks.components.NoHourlyDataAvailable
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun PrecipitationScreen(navController: NavController, index: Int = 0, locationId: String) {

    val viewModel: BlocksScreenViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.getUnitsOnce()
        viewModel.getWeather(locationId)
    }


    val uiState = viewModel.uiState.value
    val weather = uiState.weather
    val hourly = weather?.hourly ?: return
    val context = LocalContext.current
    val units = uiState.units


    val fullDayHourly =
        findMatchingHourly(hourly, weather.daily[index].time, weather.location.source)
    val date = toDateString(weather.daily[index].time, weather.location.timezone)

    val max = weather.daily[index].rainSum
    val min = fullDayHourly.minOf { it.rain }

    val maxSnow = weather.daily[index].snowfallSum ?: 0.0
    val minSnow = fullDayHourly.minOf { it.snowfall ?: 0.0 }
    val snowData = fullDayHourly.map { it.snowfall }
    val probability = fullDayHourly.map { it.precipitationProbability }


    LargeTopBarScaffold(
        title = stringResource(R.string.weather_precipitation),
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
                max = max,
                min = min,
                times = fullDayHourly.map { it.time },
                values = fullDayHourly.map { it.rain },
                probability = if (!probability.all { it == null }) fullDayHourly.map {
                    it.precipitationProbability ?: 0
                } else null,
                zoneId = weather.location.timezone,
                context = context,
                unit = units.precipitationUnit
            )
            Gap(14.dp)
            AboutCard { AboutCardText(stringResource(R.string.weather_about_precipitation)) }
            Gap(14.dp)
            SnowHeader()
            if (!snowData.contains(null)) {
                BarChart(
                    max = maxSnow,
                    min = minSnow,
                    times = fullDayHourly.map { it.time },
                    values = fullDayHourly.map { it.snowfall ?: 0.0 },
                    probability = if (!probability.all { it == null }) if (!snowData.all { it == 0.0 }) fullDayHourly.map {
                        it.precipitationProbability ?: 0
                    } else listOf(0) else null,
                    zoneId = weather.location.timezone,
                    context = context,
                    unit = units.precipitationUnit
                )
            } else {
                NoHourlyDataAvailable()
            }
            Gap(14.dp)
            AboutCard { AboutCardText(stringResource(R.string.weather_about_snowfall)) }
            Gap(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 30.dp)
        }


    }
}

@Composable
private fun BarChart(
    max: Double,
    min: Double,
    times: List<Long>,
    values: List<Double>,
    zoneId: String,
    unit: PrecipitationUnit,
    probability: List<Int>?,
    context: Context
) {


    val is24hr = LocalAppPrefs.current.is24HrTimeFormat

    val bottomValues = times.slice(6..times.lastIndex step 6)

    val barHeights = values.map {
        val percentage = ((it.minus(min)).div((max - min))).times(100)
        if (!percentage.isNaN()) max((percentage.div(100)).times(160).roundToInt(), 5) else 5
    }

    val gap = 6

    val sideValues = (gap downTo 0).map { i ->
        i * (max / gap)
    }

    val barColor = values.map {
        when {
            it <= 0.0 -> Color(0xFFE0E0E0)

            it < 1.0 -> Color(0xFFB3E5FC)
            it < 2.5 -> Color(0xFF4FC3F7)
            it < 7.5 -> Color(0xFF1E88E5)
            it < 20.0 -> Color(0xFF1565C0)
            it < 50.0 -> Color(0xFF6A1B9A)

            else -> Color(0xFFD50000)
        }
    }

    val steps = 5

    val topValues = if (probability != null) (0 until steps).map { i ->
        val index = ((probability.size - 1) * i.toDouble() / (steps - 1)).toInt()
        probability[index]
    } else null



    MatBarChart(
        topValues = topValues?.map {
            {
                Text(
                    if (probability == null) "-" else "${it}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        } ?: emptyList(),
        bottomValues = bottomValues.map {
            {

                val time = if (is24hr) to24HourTimeString(
                    it,
                    zoneId
                ) else to12HourTimeString(it, zoneId)


                Text(time, style = MaterialTheme.typography.labelMedium)
            }
        },
        sideValues = sideValues.map { "%.1f".format(it) },
        values = values,
        barHeights = barHeights,
        barColor = barColor,
        headerValue = "%.1f".format(max),
        headerSuffix = unit.toName(context, true),
        headerTitle = stringResource(R.string.weather_max_for_the_day),
        chartHeight = 250.dp
    )
}

@Composable
private fun SnowHeader() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(
            5.dp, alignment = Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 18.dp, bottom = 6.dp)
    ) {
        Symbol(
            R.drawable.snowflake_24px,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            stringResource(R.string.weather_snow_block),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
    }


}