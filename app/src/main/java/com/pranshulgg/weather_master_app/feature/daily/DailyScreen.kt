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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.components.NavigateUpBtn
import com.pranshulgg.weather_master_app.feature.daily.ui.DailyDaysHeader
import com.pranshulgg.weather_master_app.feature.daily.ui.DailyForecastHeroHeader
import com.pranshulgg.weather_master_app.feature.shared.components.blocks.WeatherBlocks
import com.pranshulgg.weather_master_app.feature.shared.ui.HourlyCard
import com.pranshulgg.weather_master_app.feature.shared.ui.SummaryCard


@Composable
fun DailyScreen(
    navController: NavController,
    index: Int = 0
) {

    val viewModel: DailyScreenViewModel = hiltViewModel()
    val context = LocalContext.current
    val prefs = LocalAppPrefs.current
    val isShowSummary = prefs.isShowSummary
    val locationStore = viewModel.location.collectAsState().value
    val weatherStore = viewModel.weather.collectAsState().value
    val unitsStore = viewModel.units.collectAsState().value
    val weatherBlocks = viewModel.weatherBlocks.collectAsState().value

    var selectedIndex by rememberSaveable { mutableIntStateOf(index) }



    if (weatherStore.weather == null) return

    var selectedDaily by remember { mutableStateOf(weatherStore.weather.daily[index]) }

    LaunchedEffect(selectedIndex) {
        selectedDaily = weatherStore.weather.daily[selectedIndex]
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
                weatherStore.weather,
                unitsStore.units,
                onSelect = { selectedIndex = it },
                selectedIndex
            )


            DailyForecastHeroHeader(selectedDaily, locationStore.activeLocation!!, unitsStore.units)

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (isShowSummary) {
                    SummaryCard(
                        weatherStore.weather, context = context,
                        dailyIndex = selectedIndex,
                        units = unitsStore.units,
                    )
                }
                HourlyCard(
                    weatherStore.weather,
                    unitsStore.units,
                    if (selectedIndex != 0) selectedDaily.time else null,
                    isDaily = true
                )
                WeatherBlocks(
                    weather = weatherStore.weather,
                    airQuality = null,
                    units = unitsStore.units,
                    context = context,
                    blocks = weatherBlocks,
                    onUpdateBlocks = {
                        viewModel.saveBlocks(it)
                    },
                    isDaily = true,
                    dailyIndex = selectedIndex,
                    navController = navController
                )

                Gap(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding())
            }
        }
    }
}
