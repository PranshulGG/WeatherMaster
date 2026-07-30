package com.pranshulgg.weather_master_app.feature.editlocation.ui

import androidx.compose.runtime.Composable
import com.pranshulgg.weather_master_app.core.ui.components.TextAlertDialog
import com.pranshulgg.weather_master_app.feature.editlocation.EditLocationViewModel
import com.pranshulgg.weather_master_app.feature.locations.LocationsScreenViewModel
import com.pranshulgg.weather_master_app.feature.shared.WeatherViewModel

object EditLocationScreenDialogs {
    @Composable
    fun EditLocationScreenConfirmationDialog(
        viewModel: EditLocationViewModel,
        onConfirm: () -> Unit
    ) {
        val uiState = viewModel.uiState.value

        TextAlertDialog(
            show = uiState.isConfirmationDialogOpen,
            onConfirm = onConfirm,
            onDismiss = viewModel::hideConfirmationDialog,
            title = "Delete Location",
            message = "Are you sure you want to delete this location?"
        )
    }
}