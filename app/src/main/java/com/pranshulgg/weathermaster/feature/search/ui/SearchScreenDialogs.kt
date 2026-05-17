package com.pranshulgg.weathermaster.feature.search.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.model.providers.SearchProvider
import com.pranshulgg.weathermaster.core.model.providers.toName
import com.pranshulgg.weathermaster.core.prefs.AppPrefsState
import com.pranshulgg.weathermaster.core.ui.components.DialogBasic
import com.pranshulgg.weathermaster.core.ui.components.RadioRow
import com.pranshulgg.weathermaster.feature.search.SearchScreenViewModel
import com.pranshulgg.weathermaster.feature.search.SearchUiState

object SearchDialogs {

    @Composable
    fun SearchProviderPickerDialog(
        prefs: AppPrefsState,
        viewModel: SearchScreenViewModel,
        uiState: SearchUiState,
    ) {
        var selectedProvider by remember(
            uiState.isProviderDialogOpen,
            prefs.searchProvider
        ) {
            mutableStateOf(prefs.searchProvider)
        }

        val providers = SearchProvider.entries

        DialogBasic(
            title = stringResource(R.string.search_provider),
            onConfirm = {
                viewModel.updateProvider(selectedProvider, prefs)
                viewModel.removeResults()
            },
            onDismiss = viewModel::hideProviderDialog,
            show = uiState.isProviderDialogOpen,
            confirmText = stringResource(R.string.action_save),
            dismissText = stringResource(R.string.action_cancel)
        ) {
            providers.forEach { provider ->
                RadioRow(
                    label = provider.toName(),
                    onClick = { selectedProvider = SearchProvider.valueOf(it) },
                    selected = selectedProvider == provider,
                    value = provider.toString()
                )
            }
        }
    }
}
