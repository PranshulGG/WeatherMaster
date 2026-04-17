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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.ui.components.EmptyContainerPlaceholder
import com.pranshulgg.weathermaster.core.ui.components.Gap
import com.pranshulgg.weathermaster.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weathermaster.core.ui.components.LoadingScreenPlaceholder
import com.pranshulgg.weathermaster.core.ui.components.NavigateUpBtn
import com.pranshulgg.weathermaster.core.ui.components.SettingSection
import com.pranshulgg.weathermaster.core.ui.components.SettingTile
import com.pranshulgg.weathermaster.feature.search.ui.SearchFloatingToolbar

data class SearchUiState(
    val query: String = "",
    val isSheetOpen: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchScreen(navController: NavController) {

    val viewModel: SearchScreenViewModel = hiltViewModel()
    val results = viewModel.results
    val loading = viewModel.loading
    val uiState by viewModel.uiState
    val scrollBehaviorToolbar =
        FloatingToolbarDefaults.exitAlwaysScrollBehavior(exitDirection = Bottom)


    LargeTopBarScaffold(
        title = "Search",
        navigationIcon = { NavigateUpBtn(navController) },
        bottomBar = {
            SearchFloatingToolbar(scrollBehaviorToolbar, uiState.query, viewModel)
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
                    text = "Search locations",
                    description = "Enter a city or location to view the weather"
                )
            }

            if (results.isNotEmpty() && !loading) {
                Column(
                    modifier = Modifier
                        .nestedScroll(scrollBehaviorToolbar)
                        .verticalScroll(rememberScrollState())
                ) {
                    SettingSection(
                        tiles = results.map {
                            SettingTile.ActionTile(
                                title = it.name,
                                description = if (it.state.isNotEmpty()) "${it.state}, " else "" + it.country,
                                onClick = {}
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
}

