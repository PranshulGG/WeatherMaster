package com.pranshulgg.weather_master_app.feature.daily

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
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
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlock
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.components.NavigateUpBtn
import com.pranshulgg.weather_master_app.feature.daily.ui.DailyDaysHeader
import com.pranshulgg.weather_master_app.feature.daily.ui.DailyForecastHeroHeader
import com.pranshulgg.weather_master_app.feature.shared.components.blocks.WeatherBlocks
import com.pranshulgg.weather_master_app.feature.shared.ui.HourlyCard
import com.pranshulgg.weather_master_app.feature.shared.ui.SummaryCard

data class DailyScreenUiState(
    val weather: Weather? = null,
    val units: WeatherUnits = WeatherUnits.getDefault(),
    val blocks: List<WeatherBlock> = WeatherBlock.getDefaultForDaily()
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
            DailyDaysHeader(
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
                SummaryCard(
                    weather, context = context,
                    dailyIndex = selectedIndex,
                    units = units,
                )
                HourlyCard(
                    weather,
                    units, selectedDaily.time
                )
                WeatherBlocks(
                    weather,
                    null,
                    units,
                    context,
                    uiState.blocks,
                    true,
                    updatedBlockOrder = { viewModel.updateBlocksOrder(it) },
                    selectedIndex,
                    navController
                )

                Gap(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding())
            }
        }
    }
}
