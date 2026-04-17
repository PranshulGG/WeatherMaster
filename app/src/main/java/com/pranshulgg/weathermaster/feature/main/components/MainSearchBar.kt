package com.pranshulgg.weathermaster.feature.main.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.ui.components.Symbol
import com.pranshulgg.weathermaster.core.ui.components.Tooltip
import com.pranshulgg.weathermaster.feature.locations.LocationsScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainSearchBar(
    isFroggyLayout: Boolean = true,
    paddingValues: PaddingValues,
    navController: NavController
) {
    val searchBarState = rememberSearchBarState()
    val scope = rememberCoroutineScope()
    val textFieldState = rememberTextFieldState()
    val searchExpanded = searchBarState.currentValue == SearchBarValue.Expanded


    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = searchBarState,
                onSearch = {},
                placeholder = {
                    Text(
                        modifier = Modifier.clearAndSetSemantics {},
                        text = "Location nameee",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                },

                readOnly = true,
                leadingIcon = {
                    Symbol(
                        R.drawable.search_24px,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (!searchExpanded)
                        Tooltip("Settings") {
                            IconButton(onClick = {}) {
                                Symbol(
                                    R.drawable.settings_24px,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                },
            )
        }

    SearchBar(
        modifier = Modifier.padding(
            top = paddingValues.calculateTopPadding(),
            start = 16.dp,
            end = 16.dp
        ),
        colors = SearchBarDefaults.colors(containerColor = if (isFroggyLayout) MaterialTheme.colorScheme.surfaceBright else Color.Transparent),
        state = searchBarState,
        inputField = inputField
    )


    LocationsScreen(
        searchBarState = searchBarState,
        onBack = {
            scope.launch { searchBarState.animateToCollapsed() }
        },
        navController = navController
    )
}

