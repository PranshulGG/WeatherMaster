package com.pranshulgg.weathermaster.feature.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.LoadingIndicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.pranshulgg.weathermaster.core.model.WeatherConditions
import com.pranshulgg.weathermaster.feature.main.ui.FroggyCurrentWeatherCard
import com.pranshulgg.weathermaster.feature.main.ui.backgroundGradients
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
                        FroggyCurrentWeatherCard(
                            paddingValues,
                            navController,
                            drawerState,
                            weather,
                            uiState.weatherUnits
                        )
                        HourlyCard(weather, uiState.weatherUnits)
                    }
                }
            }
        }
    }
}