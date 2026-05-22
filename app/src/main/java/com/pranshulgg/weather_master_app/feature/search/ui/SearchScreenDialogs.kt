package com.pranshulgg.weather_master_app.feature.search.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.sources.SearchSource
import com.pranshulgg.weather_master_app.core.prefs.AppPrefsState
import com.pranshulgg.weather_master_app.core.ui.components.DialogBasic
import com.pranshulgg.weather_master_app.core.ui.components.RadioRow
import com.pranshulgg.weather_master_app.feature.search.SearchScreenViewModel
import com.pranshulgg.weather_master_app.feature.search.SearchUiState

object SearchDialogs {

    @Composable
    fun SearchProviderPickerDialog(
        prefs: AppPrefsState,
        viewModel: SearchScreenViewModel,
        uiState: SearchUiState,
    ) {
        var selectedSource by remember(
            uiState.isSourceDialogOpen,
            prefs.searchSource
        ) {
            mutableStateOf(prefs.searchSource)
        }

        val sources = SearchSource.entries

        DialogBasic(
            title = stringResource(R.string.search_provider),
            onConfirm = {
                viewModel.updateSource(selectedSource, prefs)
                viewModel.removeResults()
            },
            onDismiss = viewModel::hideSourceDialog,
            show = uiState.isSourceDialogOpen,
            confirmText = stringResource(R.string.action_save),
            dismissText = stringResource(R.string.action_cancel)
        ) {
            sources.forEach { source ->
                RadioRow(
                    label = source.displayName,
                    onClick = { selectedSource = SearchSource.valueOf(it) },
                    selected = selectedSource == source,
                    value = source.toString()
                )
            }
        }
    }
}
