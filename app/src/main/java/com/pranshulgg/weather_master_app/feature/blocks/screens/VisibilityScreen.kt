package com.pranshulgg.weather_master_app.feature.blocks.screens

import android.content.Context
import android.util.Log
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
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.pranshulgg.weather_master_app.core.model.weather.DistanceUnit
import com.pranshulgg.weather_master_app.core.model.weather.PressureUnit
import com.pranshulgg.weather_master_app.core.model.weather.toName
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
import com.pranshulgg.weather_master_app.feature.blocks.components.ScaleCard
import kotlin.math.max
import kotlin.math.roundToInt

private data class VisibilityScaleRange(
    val headlineRes: Int,
    val descriptionRes: Int,
    val kmScale: String,
    val miScale: String,
    val mScale: String,
)

private data class VisibilityScaleInfo(
    val headline: String,
    val description: String,
    val scale: String
)

@Composable
fun VisibilityScreen(navController: NavController, index: Int = 0, locationId: String) {

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
    val scale = getVisibilityScaleFor(units.distanceUnit)
    val visibility = fullDayHourly.map { it.visibility }



    LargeTopBarScaffold(
        title = stringResource(R.string.weather_visibility),
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
            if (!visibility.contains(null)) {
                BarChart(
                    times = fullDayHourly.map { it.time },
                    values = fullDayHourly.map { it.visibility!! },
                    zoneId = weather.location.timezone,
                    unit = units.distanceUnit,
                    context = context
                )
            } else {
                NoHourlyDataAvailable()
            }
            Gap(14.dp)
            AboutCard {
                AboutCardText(stringResource(R.string.weather_visibility_about))
            }
            Gap(14.dp)
            ScaleCard {
                scale.forEach {
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(it.headline) },
                        supportingContent = { Text(it.description) },
                        trailingContent = {
                            Text(
                                it.scale,
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


@Composable
private fun BarChart(
    times: List<Long>,
    values: List<Int>,
    zoneId: String,
    unit: DistanceUnit,
    context: Context
) {


    val is24hr = LocalAppPrefs.current.is24HrTimeFormat


    val formatter: (Int) -> Double? = {
        DistanceUnit.M.convert(it.toDouble(), unit)
    }

    val bottomValues = times.slice(6..times.lastIndex step 6)

    val sideValues =
        (0..50000).toList().sortedByDescending { it }.slice(0..50000 step 10000)


    val barHeights = values.map {

        val visibilityMin = 0.0
        val visibilityMax = 50000.0

        val valuePercentage =
            ((it - visibilityMin) / (visibilityMax - visibilityMin))
                .coerceIn(0.0, 1.0)

        max((valuePercentage * 160).roundToInt(), 5)
    }


    val barColor = values.map {

        val visibilityKm = DistanceUnit.M.convert(it.toDouble(), DistanceUnit.KM)?.roundToInt()!!

        when {
            visibilityKm < 1 -> Color(0xFFC62828)
            visibilityKm < 5 -> Color(0xFFFB8C00)
            visibilityKm < 10 -> Color(0xFFFDD835)
            visibilityKm < 20 -> Color(0xFF43A047)
            else -> Color(0xFF1565C0)
        }
    }

    MatBarChart(
        topValues = emptyList(),
        bottomValues = bottomValues.map {
            {
                val time = if (is24hr) to24HourTimeString(
                    it,
                    zoneId
                ) else to12HourTimeString(it, zoneId)


                Text(time, style = MaterialTheme.typography.labelMedium)
            }
        },
        sideValues = sideValues.map { formatter(it)?.roundToInt().toString() },
        values = values,
        barHeights = barHeights,
        headerValue = "${values.map { formatter(it)!! }.average().roundToInt()}",
        headerSuffix = unit.toName(inShort = true, context),
        barColor = barColor,
        chartHeight = 220.dp
    )
}

private val visibilityRanges = listOf(
    VisibilityScaleRange(
        R.string.text_excellent,
        R.string.visibility_description_excellent,
        "> 50 km",
        "> 30 mi",
        "> 50000 m"
    ),
    VisibilityScaleRange(
        R.string.text_very_good,
        R.string.visibility_description_very_good,
        "20 - 50 km",
        "12 - 30 mi",
        "20000 - 50000 m"
    ),
    VisibilityScaleRange(
        R.string.text_good,
        R.string.visibility_description_good,
        "10 - 20 km",
        "6 - 12 mi",
        "10000 - 20000 m"
    ),
    VisibilityScaleRange(
        R.string.text_moderate,
        R.string.visibility_description_moderate,
        "4 - 10 km",
        "2.5 - 6 mi",
        "4000 - 10000 m"
    ),
    VisibilityScaleRange(
        R.string.text_poor,
        R.string.visibility_description_poor,
        "1 - 4 km",
        "0.6 - 2.5 mi",
        "1000 - 4000 m"
    ),
    VisibilityScaleRange(
        R.string.text_very_poor,
        R.string.visibility_description_very_poor,
        "0.2 - 1 km",
        "0.12 - 0.6 mi",
        "200 - 1000 m"
    ),
    VisibilityScaleRange(
        R.string.text_dense_fog,
        R.string.visibility_description_dense,
        "< 0.2 km",
        "< 0.12 mi",
        "< 200 m"
    )
)

@Composable
private fun getVisibilityScaleFor(unit: DistanceUnit): List<VisibilityScaleInfo> {
    return visibilityRanges.map { range ->
        VisibilityScaleInfo(
            headline = stringResource(range.headlineRes),
            scale = when (unit) {
                DistanceUnit.KM -> range.kmScale
                DistanceUnit.MI -> range.miScale
                DistanceUnit.M -> range.mScale
            },
            description = stringResource(range.descriptionRes)
        )
    }
}

