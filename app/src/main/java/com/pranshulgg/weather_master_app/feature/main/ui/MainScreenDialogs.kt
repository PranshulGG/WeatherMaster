package com.pranshulgg.weather_master_app.feature.main.ui

import androidx.compose.runtime.Composable
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
            confirmText = "Change",
            title = "Source not supported",
            message = "The currently selected source is not supported in your region. Please choose a different source"
        )
    }

}