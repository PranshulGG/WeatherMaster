package com.pranshulgg.weathermaster.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.pranshulgg.weathermaster.core.ui.theme.ShapeRadius

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DialogBasic(
    show: Boolean,
    title: String,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit,
    showDefaultActions: Boolean = true,
    confirmBtnDisabled: Boolean = false,
    content: @Composable () -> Unit,
) {
    if (!show) return

    Dialog(
        onDismissRequest = {
            onDismiss()
        }
    ) {

        Surface(
            modifier = Modifier
                .width(300.dp)
                .heightIn(max = 500.dp),
            shape = RoundedCornerShape(ShapeRadius.ExtraLarge),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shadowElevation = 6.dp
        ) {
            Column() {
                Text(
                    title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp)
                )
                Spacer(Modifier.height(16.dp))
                content()

                if (showDefaultActions) {
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp, start = 24.dp, end = 24.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(
                            onClick = {
                                onDismiss()
                            },
                            shapes = ButtonDefaults.shapes()
                        ) {
                            Text(dismissText, style = MaterialTheme.typography.labelLarge)
                        }
                        Spacer(Modifier.width(8.dp))
                        TextButton(
                            enabled = !confirmBtnDisabled,
                            onClick = {
                                onConfirm()
                                onDismiss()
                            },
                            shapes = ButtonDefaults.shapes()
                        ) {
                            Text(confirmText, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }

}
