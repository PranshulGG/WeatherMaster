package com.pranshulgg.weather_master_app.feature.main.ui.layouts

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.prefs.AppPrefs
import com.pranshulgg.weather_master_app.core.prefs.AppPrefsState
import com.pranshulgg.weather_master_app.core.ui.navigation.NavRoutes
import com.pranshulgg.weather_master_app.feature.main.MainScreenWeatherUiState
import com.pranshulgg.weather_master_app.feature.main.components.CreditsBottomSection
import com.pranshulgg.weather_master_app.feature.main.components.FroggyContainer
import com.pranshulgg.weather_master_app.feature.main.ui.AlertsSection
import com.pranshulgg.weather_master_app.feature.main.ui.CurrentWeatherCard
import com.pranshulgg.weather_master_app.feature.shared.WeatherViewModel
import com.pranshulgg.weather_master_app.feature.shared.components.blocks.WeatherBlocks
import com.pranshulgg.weather_master_app.feature.shared.ui.DailyCard
import com.pranshulgg.weather_master_app.feature.shared.ui.HourlyCard
import com.pranshulgg.weather_master_app.feature.shared.ui.SummaryCard


@Composable
fun PhoneLayout(
    weather: Weather,
    units: WeatherUnits,
    context: Context,
    isFroggyLayout: Boolean,
    navController: NavController,
    alerts: List<Alert>,
    prefs: AppPrefsState,
    viewModel: WeatherViewModel,
    onWeatherSourceInfoClick: () -> Unit,
    isShowSummary: Boolean,
    airQuality: AirQuality?,
    uiState: MainScreenWeatherUiState
) {
    CurrentWeatherCard(
        weather,
        units,
        context,
        isFroggyLayout = isFroggyLayout
    )
    if (isFroggyLayout) {
        FroggyContainer(weather)
    }
    Column(
        Modifier.padding(
            start = 16.dp,
            end = 16.dp,
            top = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (alerts.isNotEmpty()) {
            AlertsSection(
                alerts,
                prefs,
                weather.location.timezone,
                onAlertClick = {
                    navController.navigate(
                        NavRoutes.alerts(
                            weather.location.id
                        )
                    )
                }
            )
        }
        if (isShowSummary) {
            SummaryCard(
                weather,
                context = context,
                units = units
            )
        }
        HourlyCard(weather, units)
        DailyCard(weather, units, navController)
        WeatherBlocks(
            weather,
            airQuality,
            units,
            context,
            uiState.blocks,
            navController = navController,
            isAirQualityLoading = uiState.isAirQualityLoading,
            viewModel = viewModel
        )

        CreditsBottomSection(
            weather,
            onClick = onWeatherSourceInfoClick
        )
    }
}