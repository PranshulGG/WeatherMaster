package com.pranshulgg.weather_master_app.feature.blocks.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.weather.WindSpeedUnit
import com.pranshulgg.weather_master_app.core.model.weather.toName
import com.pranshulgg.weather_master_app.core.model.weather.wind.WindDirection
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.components.NavigateUpBtn
import com.pranshulgg.weather_master_app.core.utils.formatters.to12HourTimeString
import com.pranshulgg.weather_master_app.core.utils.formatters.to24HourTimeString
import com.pranshulgg.weather_master_app.core.utils.formatters.toDateString
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findMatchingHourly
import com.pranshulgg.weather_master_app.feature.blocks.BlocksScreenViewModel
import com.pranshulgg.weather_master_app.feature.blocks.components.AboutCard
import com.pranshulgg.weather_master_app.feature.blocks.components.AboutCardText
import com.pranshulgg.weather_master_app.feature.blocks.components.MatBarChart
import kotlin.math.max
import kotlin.math.roundToInt


@Composable
fun WindScreen(navController: NavController, index: Int = 0, locationId: String) {

    val viewModel: BlocksScreenViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.getUnitsOnce()
        viewModel.getWeather(locationId)
    }


    val uiState = viewModel.uiState.value
    val weather = uiState.weather
    val hourly = weather?.hourly ?: return
    val units = uiState.units
    val context = LocalContext.current


    val fullDayHourly =
        findMatchingHourly(hourly, weather.daily[index].time, weather.location.source)

    val date = toDateString(weather.daily[index].time, weather.location.timezone)

    val maxWind = fullDayHourly.maxOf { it.windSpeed ?: 0.0 }
    val minWind = fullDayHourly.minOf { it.windSpeed ?: 0.0 }
    val dayDirection = weather.daily[index].windDirection

    LargeTopBarScaffold(
        title = stringResource(R.string.weather_wind),
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
            WindBarChart(
                max = maxWind,
                min = minWind,
                times = fullDayHourly.map { it.time },
                values = fullDayHourly.map { it.windSpeed ?: 0.0 },
                directions = fullDayHourly.map { it.windDirection },
                unit = units.windUnit,
                context = context,
                dayDirection = dayDirection
            )
            Gap(14.dp)
            AboutCard {
                AboutCardText(stringResource(R.string.weather_about_windspeed))
            }
            Gap(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 30.dp)
        }
    }
}


@Composable
private fun WindBarChart(
    max: Double,
    min: Double,
    times: List<Long>,
    values: List<Double>,
    directions: List<WindDirection?>,
    dayDirection: WindDirection?,
    unit: WindSpeedUnit,
    context: Context
) {


    val is24hr = LocalAppPrefs.current.is24HrTimeFormat

    val barHeights = values.map {
        val percentage = ((it.minus(min)).div((max - min))).times(100)
        max((percentage.div(100)).times(160).roundToInt(), 5)
    }


    val sideValues =
        (0 until max.roundToInt()).sortedByDescending { it }
            .map { "${WindSpeedUnit.KPH.convert(it.toDouble(), unit)?.roundToInt()}" }.distinct()
            .take(10)


    val directions = directions.map {
        WindDirection.toDegrees(it)
    }


    val steps = 7

    val topValues = if (!directions.contains(null)) (0 until steps).map { i ->
        val index = ((directions.size - 1) * i.toDouble() / (steps - 1)).toInt()
        directions[index]
    } else null
    val bottomValues = times.slice(6..times.lastIndex step 6)


    val barColor = values.map {
        when {
            it < 10 -> Color(0xFF42A5F5)
            it < 20 -> Color(0xFF66BB6A)
            it < 40 -> Color(0xFFFDD835)
            it < 60 -> Color(0xFFFB8C00)
            else -> Color(0xFFC62828)
        }
    }
    MatBarChart(
        topValues = topValues?.map {
            {
                Image(
                    painter = painterResource(id = R.drawable.weather_wind_arrow_dominant),
                    contentDescription = "",
                    modifier = Modifier
                        .size(18.dp)
                        .rotate(it!!.toFloat()),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
            }
        } ?: emptyList(),
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
        barColor = barColor,
        headerValue = "${WindSpeedUnit.KPH.convert(max, unit)?.roundToInt()}",
        headerSuffix = "${
            unit.toName(
                context,
                true
            )
        } ${if (dayDirection != null) " • $dayDirection" else ""}",
        headerTitle = stringResource(R.string.weather_max_for_the_day),
        chartHeight = 250.dp
    )
}