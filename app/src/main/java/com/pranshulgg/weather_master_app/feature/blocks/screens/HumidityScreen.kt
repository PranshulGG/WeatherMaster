package com.pranshulgg.weather_master_app.feature.blocks.screens

import android.content.Context
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
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit
import com.pranshulgg.weather_master_app.core.model.weather.uv.getUvIndex
import com.pranshulgg.weather_master_app.core.model.weather.uv.toColor
import com.pranshulgg.weather_master_app.core.model.weather.uv.toLabel
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
import com.pranshulgg.weather_master_app.feature.blocks.components.MatBarChart
import com.pranshulgg.weather_master_app.feature.blocks.components.NoHourlyDataAvailable
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
    val units = uiState.units

    val fullDayHourly =
        findMatchingHourly(hourly, weather.daily[index].time, weather.location.source)


    val date = toDateString(weather.daily[index].time, weather.location.timezone)

    val dewPointMax = fullDayHourly.maxOf { it.dewPoint ?: 0.0 }
    val dewPointMin = fullDayHourly.minOf { it.dewPoint ?: 0.0 }

    val dewPointData = fullDayHourly.map { it.dewPoint }
    val humidityData = fullDayHourly.map { it.humidity }


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
            if (!humidityData.contains(null)) {
                BarChart(
                    times = fullDayHourly.map { it.time },
                    values = fullDayHourly.map { it.humidity?.roundToInt() ?: 0 },
                )
            } else {
                NoHourlyDataAvailable()
            }
            Gap(14.dp)
            AboutCard {
                AboutCardText(stringResource(R.string.weather_about_humidity))
            }
            Gap(14.dp)
            DewPointHeader()
            if (!dewPointData.contains(null)) {
                DewPointBarChart(
                    times = fullDayHourly.map { it.time },
                    values = fullDayHourly.map { it.dewPoint ?: 0.0 },
                    max = dewPointMax,
                    min = dewPointMin,
                    unit = units.tempUnit
                )
            } else {
                NoHourlyDataAvailable()
            }
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
    times: List<Long>,
    values: List<Int>,
) {

    val is24hr = LocalAppPrefs.current.is24HrTimeFormat

    val steps = 6
    val bottomValues = List(times.size) { index ->
        if (index % steps == 0 && index != 0) times[index] else times[0] // TIMES ARE NEVER NULL
    }

    val sideValues = (0..100).toList().sortedByDescending { it }.slice(0..100 step 10)

    val barHeights = values.map {
        max((it.div(100f)).times(160), 5f)
    }
    val barColor = values.map {

        when {
            it < 20 -> Color(0xFFC62828)
            it < 40 -> Color(0xFFF9A825)
            it < 60 -> Color(0xFF43A047)
            it < 80 -> Color(0xFF1E88E5)
            else -> Color(0xFF1565C0)
        }
    }
    MatBarChart(
        topValues = emptyList(),
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
        barHeights = barHeights.map { it.roundToInt() },
        headerValue = "${values.map { it.toDouble() }.average().roundToInt()}%",
        headerSuffix = "",
        barColor = barColor,
        chartHeight = 230.dp
    )
}


@Composable
private fun DewPointBarChart(
    max: Double,
    min: Double,
    times: List<Long>,
    values: List<Double>,
    unit: TemperatureUnit
) {

    val timeStartIndex = if (times.size == 12) 0 else 6
    val is24hr = LocalAppPrefs.current.is24HrTimeFormat

    val formatter: (Double) -> Double? = {
        TemperatureUnit.CELSIUS.convert(it, unit)
    }

    val bottomValues = times.slice(timeStartIndex..times.lastIndex step 6)

    val sideValues =
        (min.roundToInt()..max.roundToInt()).sortedByDescending { it }
            .map { "${formatter(it.toDouble())?.roundToInt()}°" }

    val barHeights = values.map {
        val percentage = ((it.minus(min)).div((max - min))).times(
            100
        ).roundToInt()
        max((percentage.div(100f)).times(170), 5f)
    }
    val barColor = values.map {
        val dewPoint = it.roundToInt()

        when {
            dewPoint < 0 -> Color(0xFF1565C0)
            dewPoint < 10 -> Color(0xFF42A5F5)
            dewPoint < 15 -> Color(0xFF66BB6A)
            dewPoint < 20 -> Color(0xFFFDD835)
            dewPoint < 24 -> Color(0xFFFB8C00)
            else -> Color(0xFFC62828)
        }
    }
    MatBarChart(
        topValues = emptyList(),
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
        barHeights = barHeights.map { it.roundToInt() },
        headerValue = "${values.mapNotNull { formatter(it) }.average().roundToInt()}°",
        headerSuffix = "",
        barColor = barColor,
        chartHeight = 240.dp
    )
}

@Composable
private fun DewPointHeader() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(
            5.dp, alignment = Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 18.dp, bottom = 6.dp)
    ) {
        Symbol(
            R.drawable.dew_point_24px,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            stringResource(R.string.weather_dew_point),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
    }


}