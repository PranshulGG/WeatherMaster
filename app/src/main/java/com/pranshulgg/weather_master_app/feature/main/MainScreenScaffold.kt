package com.pranshulgg.weather_master_app.feature.main

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.core.managers.WeatherBlocksManager
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherBlock
import com.pranshulgg.weather_master_app.core.model.domain.weather.WeatherUnits
import com.pranshulgg.weather_master_app.core.prefs.AppPrefsState
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.data.store.WeatherBlocksStore
import com.pranshulgg.weather_master_app.data.store.WeatherBlocksStoreState
import com.pranshulgg.weather_master_app.data.store.WeatherStoreState
import com.pranshulgg.weather_master_app.data.store.WeatherUnitsStore
import com.pranshulgg.weather_master_app.data.store.WeatherUnitsStoreState
import com.pranshulgg.weather_master_app.feature.main.components.MainSearchBar
import com.pranshulgg.weather_master_app.feature.main.ui.BackgroundGradient
import com.pranshulgg.weather_master_app.feature.main.ui.layouts.PhoneLayout
import com.pranshulgg.weather_master_app.feature.main.ui.layouts.TabletLayout
import com.pranshulgg.weather_master_app.feature.main.ui.weatherAnimations.WeatherAnimations
import com.pranshulgg.weather_master_app.feature.shared.WeatherViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainScreenScaffold(
    navController: NavController,
    drawerState: DrawerState,
    weatherStore: WeatherStoreState,
    onRefresh: () -> Unit,
    onEditLocation: () -> Unit,
    context: Context,
    onWeatherSourceInfoClick: () -> Unit,
    isTabletLike: Boolean = false,
    prefs: AppPrefsState,
    units: WeatherUnitsStoreState,
    isLoading: Boolean,
    activeLocation: Location?,
    weatherBlocks: WeatherBlocksStoreState,
    onUpdateBlocks: (List<WeatherBlock>) -> Unit,
) {


    val pullToRefreshState = rememberPullToRefreshState()
    val weather = remember(weatherStore.weather) { weatherStore.weather }
    val airQuality = remember(weatherStore.airQuality) { weatherStore.airQuality }
    val alerts = remember(weatherStore.alerts) { weatherStore.alerts }

    val layoutDirection = LocalLayoutDirection.current

    val units = units.units
    val scrollState = rememberScrollState()

    val isFroggyLayout = prefs.isFroggyLayout
    val isShowWeatherAnimations = prefs.isShowWeatherAnimations
    val isWeatherBasedTheme = prefs.isWeatherBasedTheme
    val isShowSummary = prefs.isShowSummary

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

    Scaffold(
        containerColor = if (isWeatherBasedTheme) Color.Black else MaterialTheme.colorScheme.surfaceContainerHigh,
    ) { paddingValues ->
        Box() {


            if (isWeatherBasedTheme) {
                BackgroundGradient(weather, isScrolled)
            }
            if (isShowWeatherAnimations) {
                AnimatedVisibility(
                    visible = isAnimationVisible,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    weather?.let {
                        WeatherAnimations(
                            weather,
                            isFroggyLayout
                        )
                    }
                }
            }

            PullToRefreshBox(
                isRefreshing = isLoading,
                state = pullToRefreshState,
                onRefresh = {
                    onRefresh()
                },
                indicator = {
                    LoadingIndicator(
                        pullToRefreshState,
                        isRefreshing = isLoading,
                        modifier = Modifier
                            .zIndex(99999f)
                            .padding(top = paddingValues.calculateTopPadding() + 8.dp + 56.dp)
                            .align(Alignment.TopCenter)
                    )
                },
            ) {
                AnimatedContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState),
                    targetState = weather,
                    contentKey = { it?.location?.id },
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    }
                ) { weather ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {


                        MainSearchBar(
                            isFroggyLayout = isFroggyLayout,
                            paddingValues = paddingValues,
                            navController,
                            drawerState,
                            activeLocation,
                            onEditLocation,
                            layoutDirection
                        )
                        if (weather != null) {
                            if (!isTabletLike) {
                                PhoneLayout(
                                    weather,
                                    units,
                                    context,
                                    isFroggyLayout,
                                    navController,
                                    alerts,
                                    prefs,
                                    onWeatherSourceInfoClick,
                                    isShowSummary,
                                    airQuality,
                                    weatherBlocks,
                                    onUpdateBlocks
                                )
                            } else {
                                TabletLayout(
                                    weather,
                                    units,
                                    context,
                                    isFroggyLayout,
                                    navController,
                                    alerts,
                                    prefs,
                                    onWeatherSourceInfoClick,
                                    isShowSummary,
                                    airQuality,
                                    paddingValues,
                                    layoutDirection,
                                    weatherBlocks,
                                    onUpdateBlocks
                                )
                            }
                        }
                    }
                }
            }
        }
    }


}


