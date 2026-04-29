package com.pranshulgg.weathermaster.feature.main

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.LoadingIndicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weathermaster.core.model.domain.AppWeatherUnits
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.model.domain.WeatherBlockType
import com.pranshulgg.weathermaster.feature.main.components.FroggyContainer
import com.pranshulgg.weathermaster.feature.main.components.MainSearchBar
import com.pranshulgg.weathermaster.feature.main.ui.BackgroundGradient
import com.pranshulgg.weathermaster.feature.main.ui.CurrentWeatherCard
import com.pranshulgg.weathermaster.feature.main.ui.WeatherBlocks
import com.pranshulgg.weathermaster.feature.main.ui.weatherAnimations.WeatherAnimations
import com.pranshulgg.weathermaster.feature.shared.WeatherViewModel
import com.pranshulgg.weathermaster.feature.shared.components.blocks.HumidityBlock
import com.pranshulgg.weathermaster.feature.shared.components.blocks.MoonBlock
import com.pranshulgg.weathermaster.feature.shared.components.blocks.PressureBlock
import com.pranshulgg.weathermaster.feature.shared.components.blocks.SunBlock
import com.pranshulgg.weathermaster.feature.shared.components.blocks.UvIndexBlock
import com.pranshulgg.weathermaster.feature.shared.components.blocks.VisibilityBlock
import com.pranshulgg.weathermaster.feature.shared.ui.DailyCard
import com.pranshulgg.weathermaster.feature.shared.ui.HourlyCard
import sh.calvin.reorderable.DragGestureDetector
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState

@Composable
fun MainScreenScaffold(
    navController: NavController,
    drawerState: DrawerState,
    uiState: MainScreenUiState,
    onRefresh: () -> Unit,
    isTabletLike: Boolean = false
) {


    val pullToRefreshState = rememberPullToRefreshState()
    val weather = uiState.weather
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
                            isTabletLike
                        )
                        if (weather != null) {
                            CurrentWeatherCard(weather, units)
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
                                DailyCard(weather, units)
                                WeatherBlocks(weather, units)
                            }
                        }
                    }
                }
            }
        }
    }
}


