package com.pranshulgg.weather_master_app.feature.blocks.screens.precipitation

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.components.NavigateUpBtn
import com.pranshulgg.weather_master_app.core.utils.formatters.toDateString
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findMatchingHourly
import com.pranshulgg.weather_master_app.feature.blocks.BlocksScreenViewModel
import com.pranshulgg.weather_master_app.feature.blocks.components.AboutCard
import com.pranshulgg.weather_master_app.feature.blocks.components.AboutCardText
import com.pranshulgg.weather_master_app.feature.blocks.screens.precipitation.components.RainHourlyCard

@Composable
fun RainScreen(navController: NavController, index: Int = 0, locationId: String) {

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
    val zoneId = weather.location.timezone
    val time = if (index != 0) weather.daily[index].time else weather.current.time


    val data = findMatchingHourly(
        hourly,
        time,
        weather.location.source,

        )
    val date = toDateString(weather.daily[index].time, weather.location.timezone)


    val isOnlyPrecipitation = !weather.location.source.providesSnowFall()


    LargeTopBarScaffold(
        title = stringResource(if (isOnlyPrecipitation) R.string.weather_precipitation else R.string.weather_rain_block),
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
//            BarChart(
//                max = max,
//                min = min,
//                times = fullDayHourly.map { it.time },
//                values = fullDayHourly.map { it.rain },
//                probability = if (!probability.all { it == null }) fullDayHourly.map {
//                    it.precipitationProbability ?: 0
//                } else null,
//                zoneId = weather.location.timezone,
//                context = context,
//                unit = units.precipitationUnit
//            )
            RainHourlyCard(data, zoneId, units.precipitationUnit, context)
            Gap(14.dp)
            AboutCard { AboutCardText(stringResource(R.string.weather_about_precipitation)) }
//            Gap(14.dp)
//            if (!snowData.contains(null)) {
//                BarChart(
//                    max = maxSnow,
//                    min = minSnow,
//                    times = fullDayHourly.map { it.time },
//                    values = fullDayHourly.map { it.snowfall ?: 0.0 },
//                    probability = if (!probability.all { it == null }) if (!snowData.all { it == 0.0 }) fullDayHourly.map {
//                        it.precipitationProbability ?: 0
//                    } else listOf(0) else null,
//                    zoneId = weather.location.timezone,
//                    context = context,
//                    unit = units.precipitationUnit
//                )
//            } else {
//                NoHourlyDataAvailable()
//            }
//            Gap(14.dp)
//            AboutCard { AboutCardText(stringResource(R.string.weather_about_snowfall)) }
            Gap(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 30.dp)
        }


    }
}