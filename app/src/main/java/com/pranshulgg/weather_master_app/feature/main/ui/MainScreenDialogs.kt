package com.pranshulgg.weather_master_app.feature.main.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.components.TextAlertDialog

object MainScreenDialogs {


    @Composable
    fun UnsupportedSelectedSourceDialog(
        show: Boolean,
        onDismiss: () -> Unit,
        onConfirm: () -> Unit
    ) {
        TextAlertDialog(
            show,
            onDismiss = onDismiss,
            onConfirm = onConfirm,
            confirmText = stringResource(R.string.action_change),
            title = stringResource(R.string.error_source_not_supported),
            message = stringResource(R.string.error_source_not_supported_message)
        )
    }

}