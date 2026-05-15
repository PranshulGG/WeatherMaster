package com.pranshulgg.weathermaster.feature.daily

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.model.domain.AppWeatherUnits
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.model.domain.WeatherBlock
import com.pranshulgg.weathermaster.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weathermaster.core.ui.components.NavigateUpBtn
import com.pranshulgg.weathermaster.core.utils.formatters.toMilliseconds
import com.pranshulgg.weathermaster.feature.daily.ui.DailySelectableItemsContainer
import com.pranshulgg.weathermaster.feature.daily.ui.DailyForecastHeroHeader
import com.pranshulgg.weathermaster.feature.shared.components.blocks.WeatherBlocks
import com.pranshulgg.weathermaster.feature.shared.ui.HourlyCard
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

data class DailyScreenUiState(
    val weather: Weather? = null,
    val units: AppWeatherUnits = AppWeatherUnits.getDefault(),
    val blocks: List<WeatherBlock> = WeatherBlock.getDefault()
)

@Composable
fun DailyScreen(navController: NavController, index: Int = 0, locationId: String) {

    val viewModel: DailyScreenViewModel = hiltViewModel()
    val uiState = viewModel.uiState.value
    val weather = uiState.weather
    val units = uiState.units
    val context = LocalContext.current


    var selectedIndex by remember { mutableIntStateOf(index) }

    LaunchedEffect(Unit) {
        viewModel.loadBlocks()
        viewModel.getUnitsOnce()
        viewModel.getDailyWeather(locationId)
    }


    if (weather == null) return

    var selectedDaily by remember { mutableStateOf(weather.daily[index]) }

    LaunchedEffect(selectedIndex) {
        selectedDaily = weather.daily[selectedIndex]
    }


    val hourlyStartTime =
        if (selectedIndex == 0) ZonedDateTime.now(ZoneId.of(weather.location.timezone))
            .toEpochSecond()
            .toMilliseconds() else Instant.ofEpochMilli(selectedDaily.time)
            .atZone(ZoneId.of(weather.location.timezone)).toEpochSecond()
            .toMilliseconds()


    LargeTopBarScaffold(
        title = stringResource(R.string.weather_daily_forecast),
        navigationIcon = { NavigateUpBtn(navController) },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = paddingValues.calculateTopPadding())
        ) {
            DailySelectableItemsContainer(
                weather,
                units,
                onSelect = { selectedIndex = it },
                selectedIndex
            )

            DailyForecastHeroHeader(selectedDaily, weather.location, units)

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                HourlyCard(
                    weather,
                    units, hourlyStartTime
                )
                WeatherBlocks(
                    weather,
                    null,
                    units,
                    context,
                    uiState.blocks,
                    true,
                    updatedBlockOrder = { viewModel.updateBlocksOrder(it) }, selectedIndex
                )
            }
        }
    }
}
