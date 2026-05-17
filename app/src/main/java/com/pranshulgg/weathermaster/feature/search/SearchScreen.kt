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
import com.pranshulgg.weathermaster.core.model.providers.SearchProvider
import com.pranshulgg.weathermaster.core.model.providers.toName
import com.pranshulgg.weathermaster.core.model.weather.toName
import com.pranshulgg.weathermaster.core.prefs.LocalAppPrefs
import com.pranshulgg.weathermaster.core.ui.components.DialogBasic
import com.pranshulgg.weathermaster.core.ui.components.EmptyContainerPlaceholder
import com.pranshulgg.weathermaster.core.ui.components.Gap
import com.pranshulgg.weathermaster.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weathermaster.core.ui.components.LoadingScreenPlaceholder
import com.pranshulgg.weathermaster.core.ui.components.NavigateUpBtn
import com.pranshulgg.weathermaster.core.ui.components.RadioRow
import com.pranshulgg.weathermaster.core.ui.components.SettingSection
import com.pranshulgg.weathermaster.core.ui.components.SettingTile
import com.pranshulgg.weathermaster.core.ui.components.Symbol
import com.pranshulgg.weathermaster.feature.search.ui.SearchDialogs
import com.pranshulgg.weathermaster.feature.search.ui.SearchFloatingToolbar

data class SearchUiState(
    val query: String = "",
    val isSheetOpen: Boolean = false,
    val provider: SearchProvider = SearchProvider.OPEN_METEO,
    val isProviderDialogOpen: Boolean = false
)


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchScreen(navController: NavController) {

    val viewModel: SearchScreenViewModel = hiltViewModel()
    val results = viewModel.results
    val loading = viewModel.loading
    val uiState by viewModel.uiState
    val prefs = LocalAppPrefs.current
    var itemLoadingId by remember { mutableStateOf("") }



    LaunchedEffect(Unit) {
        viewModel.updateProvider(prefs.searchProvider, prefs)
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
                    viewModel.showProviderDialog()
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
                            prefs.searchProvider.toName()
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
                                    if (itemLoadingId == it.id) {
                                        LoadingIndicator()
                                    }
                                },
                                onClick = {
                                    if (itemLoadingId.isNotBlank()) return@ActionTile

                                    viewModel.saveLocation(
                                        it,
                                        onBack = { navController.popBackStack() },
                                        onReset = { itemLoadingId = "" }
                                    )

                                    itemLoadingId = it.id
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
}

