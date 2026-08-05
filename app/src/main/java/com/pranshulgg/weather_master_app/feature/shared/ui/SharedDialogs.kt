package com.pranshulgg.weather_master_app.feature.shared.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.components.DialogBasic
import com.pranshulgg.weather_master_app.core.ui.components.TextAlertDialog
import com.pranshulgg.weather_master_app.feature.shared.components.ChangelogContent

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
            title = "Location permission",
            message = "Allow location access to save places using your current position. Your location will only be shared with the sources you choose"
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
            title = "Background location permission",
            message = "Allow background location access to keep your saved device location updated even when the app is closed or running in the background"
        )
    }


}