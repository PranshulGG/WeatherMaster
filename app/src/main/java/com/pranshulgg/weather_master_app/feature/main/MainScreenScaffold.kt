package com.pranshulgg.weather_master_app.feature.main

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.LoadingIndicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.feature.main.components.FroggyContainer
import com.pranshulgg.weather_master_app.feature.main.components.MainSearchBar
import com.pranshulgg.weather_master_app.feature.main.components.CreditsBottomSection
import com.pranshulgg.weather_master_app.feature.main.ui.BackgroundGradient
import com.pranshulgg.weather_master_app.feature.main.ui.CurrentWeatherCard
import com.pranshulgg.weather_master_app.feature.shared.components.blocks.WeatherBlocks
import com.pranshulgg.weather_master_app.feature.main.ui.weatherAnimations.WeatherAnimations
import com.pranshulgg.weather_master_app.feature.shared.ui.DailyCard
import com.pranshulgg.weather_master_app.feature.shared.ui.HourlyCard

@Composable
fun MainScreenScaffold(
    navController: NavController,
    drawerState: DrawerState,
    uiState: MainScreenWeatherUiState,
    onRefresh: () -> Unit,
    onEditLocation: () -> Unit,
    isTabletLike: Boolean = false,
    context: Context,
    onWeatherSourceInfoClick: () -> Unit
) {


    val pullToRefreshState = rememberPullToRefreshState()
    val weather = remember(uiState.weather) { uiState.weather }
    val airQuality = remember(uiState.airQuality) { uiState.airQuality }

    val units = uiState.weatherUnits
    val scrollState = rememberScrollState()

    val isAnimationVisible by remember {
        derivedStateOf {
            scrollState.value < 30
        }
    }

    val isScrolled by remember {
        derivedStateOf {
            scrollState.value > 100
        }
    }

    Scaffold { paddingValues ->
        Box {


            BackgroundGradient(weather, isScrolled)

            AnimatedVisibility(visible = isAnimationVisible, enter = fadeIn(), exit = fadeOut()) {
                weather?.let { WeatherAnimations(weather) }
            }

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
                            .padding(top = paddingValues.calculateTopPadding() + 8.dp + 56.dp)
                            .align(Alignment.TopCenter)
                    )
                },
            ) {
                AnimatedContent(
                    targetState = weather,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    }
                ) { weather ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        MainSearchBar(
                            isFroggyLayout = true,
                            paddingValues = paddingValues,
                            navController,
                            drawerState,
                            uiState.activeLocation,
                            isTabletLike,
                            onEditLocation
                        )
                        if (weather != null) {
                            CurrentWeatherCard(weather, units, context)
                            FroggyContainer(weather)
                            Column(
                                Modifier.padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 24.dp,
                                    bottom = 24.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                HourlyCard(weather, units)
                                DailyCard(weather, units, navController)
                                WeatherBlocks(weather, airQuality, units, context, uiState.blocks)

                                CreditsBottomSection(
                                    weather,
                                    onClick = onWeatherSourceInfoClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }


}


