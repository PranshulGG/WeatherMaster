package com.pranshulgg.weather_master_app.feature.locations.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.components.ActionBottomSheet
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weather_master_app.feature.locations.LocationsScreenViewModel
import com.pranshulgg.weather_master_app.feature.locations.components.LocationScreenSheetContent

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