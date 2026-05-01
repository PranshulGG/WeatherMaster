package com.pranshulgg.weathermaster.feature.locations.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.ui.components.ActionBottomSheet
import com.pranshulgg.weathermaster.core.ui.components.SettingSection
import com.pranshulgg.weathermaster.core.ui.components.SettingTile
import com.pranshulgg.weathermaster.core.ui.components.SettingsTileIcon
import com.pranshulgg.weathermaster.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weathermaster.feature.locations.LocationsScreenViewModel
import com.pranshulgg.weathermaster.feature.locations.components.LocationScreenSheetContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreenSheet(
    viewModel: LocationsScreenViewModel,
    sheetState: SheetState
) {

    val uiState = viewModel.uiState.value

    if (uiState.isBottomSheetOpen)

        ActionBottomSheet(
            sheetState = sheetState,
            onConfirm = {},
            onCancel = viewModel::hideBottomSheet,
            showActions = false
        ) { hide ->

            LocationScreenSheetContent(
                locationName = uiState.longClickedLocation?.name ?: "Location action",
                onDelete = {
                    hide()
                    if (uiState.longClickedLocation?.isDefault ?: false) {
                        SnackbarManager.show(R.string.error_delete_default_location)
                        return@LocationScreenSheetContent
                    }
                    viewModel.showConfirmationDialog()
                },
                onSetAsDefault = {
                    hide()
                    if (uiState.longClickedLocation != null && !uiState.longClickedLocation.isDefault) {
                        viewModel.updateDefaultLocation(uiState.longClickedLocation.id)
                    }
                }
            )

        }

}