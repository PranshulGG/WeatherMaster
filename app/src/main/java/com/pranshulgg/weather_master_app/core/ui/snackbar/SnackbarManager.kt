package com.pranshulgg.weather_master_app.core.ui.snackbar

import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object SnackbarManager {

    data class SnackbarEvent(
        val messageResource: Int,
        val messageArgs: Any? = null,
        val actionLabel: Int? = null,
        val onAction: (() -> Unit)? = null,
        val duration: SnackbarDuration = SnackbarDuration.Short
    )

    private val _events = MutableSharedFlow<SnackbarEvent>(
        extraBufferCapacity = 1
    )
    val events = _events.asSharedFlow()

    fun show(
        messageResource: Int,
        actionLabel: Int? = null,
        messageArgs: Any? = null,
        onAction: (() -> Unit)? = null,
        duration: SnackbarDuration = SnackbarDuration.Short
    ) {
        _events.tryEmit(
            SnackbarEvent(
                messageResource = messageResource,
                actionLabel = actionLabel,
                messageArgs = messageArgs,
                onAction = onAction,
                duration = duration
            )
        )
    }
}