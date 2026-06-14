package com.pranshulgg.weather_master_app.feature.blocks.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.weather.PressureUnit
import com.pranshulgg.weather_master_app.core.model.weather.toName
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.components.NavigateUpBtn
import com.pranshulgg.weather_master_app.core.ui.components.Symbol
import com.pranshulgg.weather_master_app.core.utils.formatters.formatLocalizedNumber
import com.pranshulgg.weather_master_app.core.utils.formatters.to12HourTimeString
import com.pranshulgg.weather_master_app.core.utils.formatters.to24HourTimeString
import com.pranshulgg.weather_master_app.core.utils.formatters.toDateString
import com.pranshulgg.weather_master_app.core.utils.locale.getCurrentAppLocale
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findMatchingHourly
import com.pranshulgg.weather_master_app.feature.blocks.BlocksScreenViewModel
import com.pranshulgg.weather_master_app.feature.blocks.components.AboutCard
import com.pranshulgg.weather_master_app.feature.blocks.components.AboutCardText
import com.pranshulgg.weather_master_app.feature.blocks.components.MatBarChart
import com.pranshulgg.weather_master_app.feature.blocks.components.NoHourlyDataAvailable
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt


@Composable
fun PressureScreen(navController: NavController, index: Int = 0, locationId: String) {

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
    val pressureData = fullDayHourly.map { it.pressureMsl }


    LargeTopBarScaffold(
        title = stringResource(R.string.weather_pressure),
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

            if (!pressureData.contains(null)) {
                BarChart(
                    times = fullDayHourly.map { it.time },
                    values = fullDayHourly.map { it.pressureMsl ?: 0.0 },
                    zoneId = weather.location.timezone,
                    unit = units.pressureUnit,
                    context = context
                )
            } else {
                NoHourlyDataAvailable()
            }
            Gap(14.dp)
            AboutCard {
                AboutCardText(stringResource(R.string.weather_about_pressure))
            }

            Gap(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 30.dp)

        }
    }
}


@Composable
private fun BarChart(
    times: List<Long>,
    values: List<Double>,
    zoneId: String,
    unit: PressureUnit,
    context: Context
) {

    val is24hr = LocalAppPrefs.current.is24HrTimeFormat

    val sideValues = when (unit) {
        PressureUnit.HPA -> listOf(980, 995, 1010, 1025, 1040)
        PressureUnit.INHG -> listOf(28, 29, 30, 31)
        else -> listOf(980, 995, 1010, 1025, 1040)
    }.sortedByDescending { it }

    val pressureIcons = values.mapIndexed { index, it ->
        val previousPressure = if (index > 0) values[index - 1] else it
        val pressureDifference = it - previousPressure
        when {
            pressureDifference > 0.5 -> R.drawable.trending_up_24px
            pressureDifference < -0.5 -> R.drawable.trending_down_24px
            else -> R.drawable.trending_flat_24px

        }
    }

    val barHeights = values.map {

        val chartMin = 980.0
        val chartMax = 1040.0

        val valuePercentage =
            ((it - chartMin) / (chartMax - chartMin))
                .coerceIn(0.0, 1.0)

        max((valuePercentage * 170).roundToInt(), 48)
    }

    val formatter: (Double) -> Double? = {
        PressureUnit.HPA.convert(it, unit)
    }
    val steps = 7

    val topValues = (0 until steps).map { i ->
        val index = ((pressureIcons.size - 1) * i.toDouble() / (steps - 1)).toInt()
        pressureIcons[index]
    }


    val bottomValues = times.slice(6..times.lastIndex step 6)

    val barColor = values.map {
        val pressure = it.roundToInt()
        when {
            pressure < 980 -> Color(0xFF4A148C)
            pressure < 995 -> Color(0xFF1565C0)
            pressure < 1010 -> Color(0xFF43A047)
            pressure < 1025 -> Color(0xFFF9A825)
            else -> Color(0xFFC62828)
        }
    }

    MatBarChart(
        topValues = topValues.map { { Symbol(it, size = 18.dp) } },
        bottomValues = bottomValues.map {
            {

                val time = if (is24hr) to24HourTimeString(
                    it,
                    zoneId
                ) else to12HourTimeString(it, zoneId)


                Text(time, style = MaterialTheme.typography.labelMedium)
            }
        },
        sideValues = sideValues.map { formatter(it.toDouble())?.roundToInt()!! },
        values = values,
        barHeights = barHeights,
        headerValue = formatLocalizedNumber(
            locale = getCurrentAppLocale(),
            number = formatter(values.average())!!,
            decimalPlaces = 1
        ),
        headerSuffix = unit.toName(inShort = true, context),
        barColor = barColor,
        chartHeight = 220.dp
    )


}
