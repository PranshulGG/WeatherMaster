package com.pranshulgg.weathermaster.feature.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.pranshulgg.weathermaster.core.model.Location
import com.pranshulgg.weathermaster.core.model.Weather
import com.pranshulgg.weathermaster.core.prefs.LocalAppPrefs
import com.pranshulgg.weathermaster.feature.locations.LocationsScreen
import com.pranshulgg.weathermaster.feature.locations.LocationsScreenViewModel
import com.pranshulgg.weathermaster.feature.shared.WeatherViewModel
import kotlinx.coroutines.launch

data class MainScreenUiState(
    val isError: Boolean = false,
    val isLoading: Boolean = false,
    val activeLocation: Location? = null,
    val locations: List<Location> = emptyList(),
    val weather: Weather? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {

    val weatherViewModel: WeatherViewModel = hiltViewModel()
    val uiState by weatherViewModel.uiState
    val activeLocation = uiState.activeLocation
    val prefs = LocalAppPrefs.current


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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                LocationsScreen(
                    onBack = { closeDrawer() },
                    navController,
                    uiState.locations,
                    uiState.activeLocation,
                    weatherViewModel::setActiveLocation
                )
            }
        },
    ) {
        MainScreenScaffold(
            navController,
            drawerState,
            uiState,
            onRefresh = {
                if (activeLocation != null)
                    weatherViewModel.getWeather(
                        activeLocation,
                        activeLocation.provider
                    )
            })

    }
}



