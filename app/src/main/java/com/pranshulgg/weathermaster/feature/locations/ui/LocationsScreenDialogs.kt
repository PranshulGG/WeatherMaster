package com.pranshulgg.weathermaster.feature.locations.ui

import androidx.compose.runtime.Composable
import com.pranshulgg.weathermaster.core.ui.components.TextAlertDialog
import com.pranshulgg.weathermaster.feature.locations.LocationsScreenViewModel
import com.pranshulgg.weathermaster.feature.shared.WeatherViewModel

@Composable
fun LocationScreenConfirmationDialog(
    weatherViewModel: WeatherViewModel,
    viewModel: LocationsScreenViewModel
) {
    val uiState = viewModel.uiState.value

    TextAlertDialog(
        show = uiState.isConfirmationDialogOpen,
        onConfirm = {
            if (uiState.longClickedLocation != null) {
                weatherViewModel.deleteLocation(uiState.longClickedLocation.id)
            }
            viewModel.hideConfirmationDialog()
        },
        onDismiss = viewModel::hideConfirmationDialog,
        title = "Delete Location",
        message = "Are you sure you want to delete this location?"
    )
}