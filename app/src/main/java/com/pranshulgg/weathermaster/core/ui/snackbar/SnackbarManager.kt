package com.pranshulgg.weathermaster.core.ui.snackbar

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object SnackbarManager {

    data class SnackbarEvent(
        val message: String,
        val actionLabel: String? = null,
        val onAction: (() -> Unit)? = null
    )

    private val _events = MutableSharedFlow<SnackbarEvent>(
        extraBufferCapacity = 1
    )
    val events = _events.asSharedFlow()

    fun show(
        message: String,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null
    ) {
        _events.tryEmit(
            SnackbarEvent(
                message = message,
                actionLabel = actionLabel,
                onAction = onAction
            )
        )
    }
}