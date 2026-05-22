package com.pranshulgg.weather_master_app.feature.search.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.sources.SearchSource
import com.pranshulgg.weather_master_app.core.prefs.AppPrefsState
import com.pranshulgg.weather_master_app.core.ui.components.ActionBottomSheet
import com.pranshulgg.weather_master_app.core.ui.components.DialogBasic
import com.pranshulgg.weather_master_app.core.ui.components.RadioRow
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.Symbol
import com.pranshulgg.weather_master_app.feature.search.SearchScreenViewModel
import com.pranshulgg.weather_master_app.feature.search.SearchUiState

object SearchScreenBottomSheets {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SearchSourcePickerSheet(
        prefs: AppPrefsState,
        viewModel: SearchScreenViewModel,
        uiState: SearchUiState,
        sheetState: SheetState
    ) {


        if (uiState.isSearchSourcePickerSheetOpen) {
            var selectedSource by remember { mutableStateOf(prefs.searchSource) }


            val sources = SearchSource.entries

            ActionBottomSheet(
                sheetState = sheetState,
                onCancel = viewModel::hideSearchSourcePickerSheet,
                onConfirm = {
                    viewModel.updateSource(selectedSource, prefs)
                    viewModel.removeResults()
                },
                confirmText = stringResource(R.string.action_save),
                cancelText = stringResource(R.string.action_cancel)
            ) {
                SettingSection(
                    title = stringResource(R.string.search_source),
                    tiles = sources.map { source ->
                        val isSelected = selectedSource == source

                        SettingTile.ActionTile(
                            leading = {
                                if (isSelected) Symbol(
                                    R.drawable.check_24px,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            },
                            title = source.displayName,
                            selected = isSelected,
                            onClick = {
                                selectedSource = source
                            }
                        )
                    }
                )
            }
        }
    }
}
