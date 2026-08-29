package com.pranshulgg.weather_master_app.feature.main.ui.layouts

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.core.model.domain.airquality.AirQuality
import com.pranshulgg.weather_master_app.core.model.domain.alerts.Alert
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.prefs.AppPrefsState
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.navigation.NavRoutes
import com.pranshulgg.weather_master_app.core.ui.theme.ShapeRadius
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
fun TabletLayout(
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
    uiState: MainScreenWeatherUiState,
    paddingValues: PaddingValues,
    layoutDirection: LayoutDirection
) {

    val startPadding = paddingValues.calculateStartPadding(layoutDirection)
        .takeIf { it > 16.dp } ?: 16.dp
    val endPadding = paddingValues.calculateEndPadding(layoutDirection)
        .takeIf { it > 16.dp } ?: 16.dp

    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = startPadding, end = endPadding)
    ) {
        Column(Modifier.weight(1f)) {

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

                CreditsBottomSection(
                    weather,
                    onClick = onWeatherSourceInfoClick
                )
            }

        }
        Gap(horizontal = 14.dp)
        Box(
            Modifier
                .weight(1f)
                .padding(top = 24.dp)
        ) {
            WeatherBlocks(
                weather,
                airQuality,
                units,
                context,
                uiState.blocks,
                navController = navController,
                isAirQualityLoading = uiState.isAirQualityLoading,
                viewModel = viewModel,
                fixedGridCells = true
            )
        }
    }

}