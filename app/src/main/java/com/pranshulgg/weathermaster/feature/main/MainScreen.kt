package com.pranshulgg.weathermaster.feature.main

import android.content.res.Configuration
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weathermaster.core.model.DistanceUnits
import com.pranshulgg.weathermaster.core.model.PrecipitationUnits
import com.pranshulgg.weathermaster.core.model.PressureUnits
import com.pranshulgg.weathermaster.core.model.TemperatureUnits
import com.pranshulgg.weathermaster.core.model.WindSpeedUnits
import com.pranshulgg.weathermaster.core.model.domain.AppWeatherUnits
import com.pranshulgg.weathermaster.core.model.domain.Location
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.prefs.LocalAppPrefs
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
    val weatherUnits: AppWeatherUnits = AppWeatherUnits.getDefault()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {

    val weatherViewModel: WeatherViewModel = hiltViewModel()
    val uiState by weatherViewModel.uiState
    val activeLocation = uiState.activeLocation
    val prefs = LocalAppPrefs.current
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

    LaunchedEffect(Unit) {
        if (prefs.isFirstStart) {
            drawerState.open()
            prefs.setFirstStart(false)
        }
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
                            isRefresh = true
                        )
                    }
                },
                isTabletLike
            )
        }
    )
}



