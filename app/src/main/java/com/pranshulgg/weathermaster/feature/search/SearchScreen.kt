package com.pranshulgg.weathermaster.feature.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.FloatingToolbarExitDirection.Companion.Bottom
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.model.domain.location.Location
import com.pranshulgg.weathermaster.core.model.sources.SearchSource
import com.pranshulgg.weathermaster.core.model.sources.WeatherSource
import com.pranshulgg.weathermaster.core.prefs.LocalAppPrefs
import com.pranshulgg.weathermaster.core.ui.components.EmptyContainerPlaceholder
import com.pranshulgg.weathermaster.core.ui.components.Gap
import com.pranshulgg.weathermaster.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weathermaster.core.ui.components.LoadingScreenPlaceholder
import com.pranshulgg.weathermaster.core.ui.components.NavigateUpBtn
import com.pranshulgg.weathermaster.core.ui.components.SettingSection
import com.pranshulgg.weathermaster.core.ui.components.SettingTile
import com.pranshulgg.weathermaster.core.ui.components.Symbol
import com.pranshulgg.weathermaster.feature.search.ui.SearchDialogs
import com.pranshulgg.weathermaster.feature.search.ui.SearchFloatingToolbar
import com.pranshulgg.weathermaster.feature.shared.ui.SharedDialogs

data class SearchUiState(
    val query: String = "",
    val isSheetOpen: Boolean = false,
    val source: SearchSource = SearchSource.OPEN_METEO,
    val isSourceDialogOpen: Boolean = false,
    val isWeatherSourcesDialogOpen: Boolean = false
)


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchScreen(navController: NavController) {

    val viewModel: SearchScreenViewModel = hiltViewModel()
    val results = viewModel.results
    val loading = viewModel.loading
    val uiState by viewModel.uiState
    val prefs = LocalAppPrefs.current
    var isLocationProcessing by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf<Location?>(null) }



    LaunchedEffect(Unit) {
        viewModel.updateSource(prefs.searchSource, prefs)
    }

    val scrollBehaviorToolbar =
        FloatingToolbarDefaults.exitAlwaysScrollBehavior(exitDirection = Bottom)



    LargeTopBarScaffold(
        title = stringResource(R.string.search),
        navigationIcon = { NavigateUpBtn(navController) },
        bottomBar = {
            SearchFloatingToolbar(scrollBehaviorToolbar, uiState, viewModel)
        },
        actions = {
            IconButton(
                onClick = {
                    viewModel.showSourceDialog()
                },
                shapes = IconButtonDefaults.shapes()
            ) { Symbol(R.drawable.settings_24px) }
        }

    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
        ) {

            if (loading) {
                LoadingScreenPlaceholder(0.8f)
            }

            if (results.isEmpty() && !loading) {
                EmptyContainerPlaceholder(
                    icon = R.drawable.search_24px,
                    text = stringResource(R.string.search_empty_state_title),
                    description = stringResource(R.string.search_empty_state_secondary)
                )
            }

            if (results.isNotEmpty() && !loading) {
                Column(
                    modifier = Modifier
                        .nestedScroll(scrollBehaviorToolbar)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        stringResource(
                            R.string.search_results_provided,
                            prefs.searchSource.displayName
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Gap(8.dp)
                    SettingSection(
                        tiles = results.map {
                            SettingTile.ActionTile(

                                title = it.name,
                                description = "${if (it.state.isNotEmpty()) "${it.state}, " else ""}${it.country}",
                                trailing = {
                                    if (isLocationProcessing) {
                                        LoadingIndicator()
                                    }
                                },
                                onClick = {
                                    if (isLocationProcessing) return@ActionTile
                                    selectedLocation = it
                                    viewModel.showWeatherSourcesDialog()
                                }
                            )
                        }
                    )

                    Gap(
                        WindowInsets.systemBars.asPaddingValues()
                            .calculateBottomPadding() + ScreenOffset + 30.dp
                    )
                }
            }
        }
    }


    // PROVIDERS DIALOG
    SearchDialogs.SearchProviderPickerDialog(prefs, viewModel, uiState)

    // WEATHER SOURCES DIALOG
    SharedDialogs.WeatherProvidersForLocationDialog(
        selectedLocation?.countryCode ?: "US",
        uiState.isWeatherSourcesDialogOpen,
        selectedLocation?.source ?: WeatherSource.OPEN_METEO,
        onSave = {
            viewModel.hideWeatherSourcesDialog()
            viewModel.saveLocation(
                selectedLocation,
                onBack = { navController.popBackStack() },
                onReset = { isLocationProcessing = false },
                it
            )

            isLocationProcessing = true
        },
        onCancel = viewModel::hideWeatherSourcesDialog
    )
}

