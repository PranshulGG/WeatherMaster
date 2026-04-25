package com.pranshulgg.weathermaster.feature.locations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.model.domain.Location
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.model.domain.WeatherCurrent
import com.pranshulgg.weathermaster.core.ui.components.NavigateUpBtn
import com.pranshulgg.weathermaster.core.ui.components.Symbol
import com.pranshulgg.weathermaster.core.ui.navigation.NavRoutes
import com.pranshulgg.weathermaster.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weathermaster.feature.shared.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LocationsScreen(
    onBack: () -> Unit,
    navController: NavController,
    locations: List<Location>,
    activeLocation: Location?,
    onLocationSelect: (Location) -> Unit

) {

    val viewModel: LocationsScreenViewModel = hiltViewModel()

    val weatherForLocations by viewModel.allLocationsWeather.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            TopBar(navController)
        },
        floatingActionButton = {
            FloatingButton(navController)
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LocationsScreenContent(
                locations,
                onDelete = { },
                onLocationSelect = {
                    onLocationSelect(it)
                },
                activeLocation = activeLocation,
                weatherForLocations
            )
        }
    }
}

@Composable
private fun TopBar(navController: NavController) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        title = {
            Text(
                "Locations",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            NavigateUpBtn(navController)
        }
    )
}

@Composable
private fun FloatingButton(navController: NavController) {
    FloatingActionButton(
        onClick = {
            navController.navigate(NavRoutes.SEARCH)
        },
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        modifier = Modifier
            .size(96.dp),
        shape = CircleShape
    ) {
        Symbol(
            R.drawable.search_24px,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            size = 36.dp
        )
    }
}