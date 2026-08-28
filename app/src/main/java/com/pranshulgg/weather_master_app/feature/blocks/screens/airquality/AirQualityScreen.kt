package com.pranshulgg.weather_master_app.feature.blocks.screens.airquality

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQualityHourly
import com.pranshulgg.weather_master_app.core.model.sources.Source
import com.pranshulgg.weather_master_app.core.model.weather.airquality.AirQualityLevel
import com.pranshulgg.weather_master_app.core.model.weather.airquality.toName
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.components.NavigateUpBtn
import com.pranshulgg.weather_master_app.core.utils.extensions.DateTimeExtensions.secondsToMilliseconds
import com.pranshulgg.weather_master_app.core.utils.formatters.toDateString
import com.pranshulgg.weather_master_app.core.utils.weather.airquality.AirQualityColors
import com.pranshulgg.weather_master_app.feature.blocks.BlocksScreenViewModel
import com.pranshulgg.weather_master_app.feature.blocks.components.NoHourlyDataAvailable
import com.pranshulgg.weather_master_app.feature.blocks.screens.airquality.components.AirQualityHourlyCard


@Composable
fun AirQualityScreen(navController: NavController, index: Int = 0, locationId: String) {

    val viewModel: BlocksScreenViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.getUnitsOnce()
        viewModel.getWeather(locationId)
        viewModel.getAirQuality(locationId)
    }


    val uiState = viewModel.uiState.value
    val airQuality = uiState.airQuality ?: return
    val weather = uiState.weather ?: return

    val hourly = airQuality.hourly
    val time = if (index != 0) weather.daily[index].time else weather.current.time
    val context = LocalContext.current
    val date = toDateString(weather.daily[index].time, weather.location.timezone)

    val data = findMatchingHourly(
        hourly,
        time,
        weather.location.source,

        ).filter { it.ozone != null || it.pm25 != null || it.pm10 != null || it.sulphurDioxide != null || it.carbonMonoxide != null || it.nitrogenDioxide != null }

    LargeTopBarScaffold(
        title = stringResource(R.string.weather_air_quality),
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

            FlowRow(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                AirQualityLevel.entries.forEach { level ->
                    Surface(
                        color = AirQualityColors.getColors(level),
                        shape = CircleShape
                    ) {
                        Text(
                            level.toName(context),
                            textAlign = TextAlign.Center,
                            color = AirQualityColors.getTextColors(level),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            if (data.isNotEmpty()) {
                AirQualityHourlyCard(data, weather.location.timezone, airQuality)
            } else {
                NoHourlyDataAvailable()
            }

            Gap(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 30.dp)

        }

    }

}


private fun findMatchingHourly(
    data: List<AirQualityHourly>,
    currentMilli: Long,
    source: Source
): List<AirQualityHourly> {


    val startIndex = data.indexOfFirst { it.time.secondsToMilliseconds() >= currentMilli }

    if (startIndex == -1) {
        return emptyList()
    }

    return data.drop(maxOf(0, startIndex)).take(source.hourlyAggregationLimitHours)


}
