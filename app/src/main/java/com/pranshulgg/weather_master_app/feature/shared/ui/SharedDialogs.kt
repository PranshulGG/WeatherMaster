package com.pranshulgg.weather_master_app.feature.shared.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.components.TextAlertDialog

object SharedDialogs {

    @Composable
    fun DeviceLocationPermissionInfoDialog(
        show: Boolean,
        onDismiss: () -> Unit,
        onConfirm: () -> Unit
    ) {
        TextAlertDialog(
            show,
            onDismiss = onDismiss,
            onConfirm = onConfirm,
            title = stringResource(R.string.location_permission),
            message = stringResource(R.string.location_permission_secondary)
        )
    }

    @Composable
    fun DeviceBackgroundLocationPermissionInfoDialog(
        show: Boolean,
        onDismiss: () -> Unit,
        onConfirm: () -> Unit
    ) {
        TextAlertDialog(
            show,
            onDismiss = onDismiss,
            onConfirm = onConfirm,
            title = stringResource(R.string.location_permission_background),
            message = stringResource(R.string.location_permission_background_secondary)
        )
    }


}