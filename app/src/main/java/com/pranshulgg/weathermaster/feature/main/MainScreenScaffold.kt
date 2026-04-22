package com.pranshulgg.weathermaster.feature.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.LoadingIndicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.pranshulgg.weathermaster.core.model.UvIndex
import com.pranshulgg.weathermaster.core.model.WeatherConditions
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.model.getUvIndex
import com.pranshulgg.weathermaster.core.ui.components.Gap
import com.pranshulgg.weathermaster.feature.main.ui.CurrentWeatherCard
import com.pranshulgg.weathermaster.feature.main.ui.backgroundGradients
import com.pranshulgg.weathermaster.feature.shared.components.blocks.HumidityBlock
import com.pranshulgg.weathermaster.feature.shared.components.blocks.PressureBlock
import com.pranshulgg.weathermaster.feature.shared.components.blocks.SunBlock
import com.pranshulgg.weathermaster.feature.shared.components.blocks.UvIndexBlock
import com.pranshulgg.weathermaster.feature.shared.components.blocks.VisibilityBlock
import com.pranshulgg.weathermaster.feature.shared.ui.DailyCard
import com.pranshulgg.weathermaster.feature.shared.ui.HourlyCard

@Composable
fun MainScreenScaffold(
    navController: NavController,
    drawerState: DrawerState,
    uiState: MainScreenUiState,
    onRefresh: () -> Unit
) {


    val pullToRefreshState = rememberPullToRefreshState()
    val weather = uiState.weather
    val units = uiState.weatherUnits



    Scaffold { paddingValues ->
        Box {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            backgroundGradients(
                                WeatherConditions.CLEAR_SKY
                            )
                        )
                    )
            )
            PullToRefreshBox(
                isRefreshing = uiState.isLoading,
                state = pullToRefreshState,
                onRefresh = {
                    onRefresh()
                },
                indicator = {
                    LoadingIndicator(
                        pullToRefreshState,
                        uiState.isLoading,
                        modifier = Modifier
                            .zIndex(99999f)
                            .align(Alignment.TopCenter)
                    )
                },
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    if (weather != null) {
                        CurrentWeatherCard(
                            paddingValues,
                            navController,
                            drawerState,
                            weather,
                            units
                        )

                        Gap(24.dp)
                        Column(
                            Modifier.padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            HourlyCard(weather, units)
                            DailyCard(weather, units)
                            SunBlock()
                        }
                    }
                }
            }
        }
    }
}