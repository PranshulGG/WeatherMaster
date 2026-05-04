package com.pranshulgg.weathermaster.feature.search

import android.util.Log
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
import com.pranshulgg.weathermaster.core.model.SearchProviders
import com.pranshulgg.weathermaster.core.model.toName
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
import com.pranshulgg.weathermaster.feature.search.ui.SearchFloatingToolbar

data class SearchUiState(
    val query: String = "",
    val isSheetOpen: Boolean = false,
    val provider: SearchProviders = SearchProviders.OPEN_METEO,
    val isProviderDialogOpen: Boolean = false
)

private data class Provider(
    val value: SearchProviders,
    val label: String
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchScreen(navController: NavController) {

    val viewModel: SearchScreenViewModel = hiltViewModel()
    val results = viewModel.results
    val loading = viewModel.loading
    val uiState by viewModel.uiState
    val prefs = LocalAppPrefs.current

    var selectedProvider by remember { mutableStateOf(prefs.searchProvider) }

    val providers = listOf(
        Provider(SearchProviders.OPEN_METEO, "Open Meteo"),
        Provider(SearchProviders.GEO_NAMES, "GeoNames")
    )

    LaunchedEffect(Unit) {
        viewModel.updateProvider(selectedProvider)
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
                    selectedProvider = prefs.searchProvider
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
                                onClick = {
                                    viewModel.saveLocation(it)
                                    navController.popBackStack()
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
    DialogBasic(
        title = stringResource(R.string.search_provider),
        onConfirm = {
            prefs.setSearchProvider(selectedProvider)
            viewModel.updateProvider(selectedProvider)
            viewModel.removeResults()
        },
        onDismiss = viewModel::hideProviderDialog,
        show = uiState.isProviderDialogOpen,
        confirmText = stringResource(R.string.action_save),
        dismissText = stringResource(R.string.action_cancel)
    ) {
        providers.forEach { provider ->
            RadioRow(
                label = provider.label,
                onClick = { selectedProvider = SearchProviders.valueOf(it) },
                selected = selectedProvider == provider.value,
                value = provider.value.toString()
            )
        }
    }
}

