package com.pranshulgg.weather_master_app.feature.editlocation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pranshulgg.weather_master_app.R
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
            title = stringResource(R.string.confirm_delete_location),
            message = stringResource(R.string.alert_delete_location_secondary)
        )
    }
}