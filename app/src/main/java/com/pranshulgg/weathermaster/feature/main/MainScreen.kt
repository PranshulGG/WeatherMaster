package com.pranshulgg.weathermaster.feature.main

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresPermission
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weathermaster.core.model.domain.AppWeatherUnits
import com.pranshulgg.weathermaster.core.model.domain.Location
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.model.domain.WeatherBlock
import com.pranshulgg.weathermaster.core.prefs.LocalAppPrefs
import com.pranshulgg.weathermaster.feature.intro.IntroScreen
import com.pranshulgg.weathermaster.feature.locations.LocationsScreen
import com.pranshulgg.weathermaster.feature.main.ui.NavigationDrawer
import com.pranshulgg.weathermaster.feature.shared.WeatherViewModel
import kotlinx.coroutines.launch

data class MainScreenUiState(
    val isError: Boolean = false,
    val isLoading: Boolean = false,
    val activeLocation: Location? = null,
    val locations: List<Location> = emptyList(),
    val weather: Weather? = null,
    val weatherUnits: AppWeatherUnits = AppWeatherUnits.getDefault(),
    val blocks: List<WeatherBlock> = WeatherBlock.getDefault()
)

@OptIn(ExperimentalMaterial3Api::class)
@RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
@Composable
fun MainScreen(navController: NavController) {
    val prefs = LocalAppPrefs.current


    if (prefs.isFirstStart) {
        IntroScreen(navController)
        return
    }

    val weatherViewModel: WeatherViewModel = hiltViewModel()
    val context = LocalContext.current
    val uiState by weatherViewModel.uiState
    val activeLocation = uiState.activeLocation
    val density = LocalDensity.current
    val widthDp = with(density) {
        LocalWindowInfo.current.containerSize.width.toDp()
    }

    val isTabletLike = widthDp > 600.dp


    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val closeDrawer = {
        scope.launch { drawerState.close() }
    }


    BackHandler(
        enabled = drawerState.isOpen,
    ) {
        closeDrawer()
    }



    NavigationDrawer(
        drawerContent = {
            LocationsScreen(
                onBack = {
                    closeDrawer()
                },
                navController,
                uiState.locations,
                uiState.activeLocation,
                onLocationSelect = {
                    if (activeLocation == it) return@LocationsScreen
                    weatherViewModel.setLoading(true)
                    if (!isTabletLike) {
                        scope.launch {
                            drawerState.close() // wait until drawer fully closes
                            weatherViewModel.setActiveLocation(it)
                        }
                    } else {
                        weatherViewModel.setActiveLocation(it)
                    }
                },
                isTabletLike
            )
        },
        drawerState = drawerState,
        isTabletLike = isTabletLike,
        content = {
            MainScreenScaffold(
                navController,
                drawerState,
                uiState,
                onRefresh = {
                    if (activeLocation != null) {
                        weatherViewModel.getWeather(
                            activeLocation,
                            activeLocation.provider,
                            isManualRefresh = true
                        )
                    }
                },
                isTabletLike,
                context
            )
        }
    )
}



