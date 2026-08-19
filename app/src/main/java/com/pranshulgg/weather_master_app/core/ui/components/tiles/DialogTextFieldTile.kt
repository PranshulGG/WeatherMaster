package com.pranshulgg.weather_master_app.core.ui.components.tiles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DialogTextFieldTile(
    headline: String,
    description: String? = null,
    initialText: String = "",
    onTextSubmitted: (String) -> Unit,
    leading: @Composable (() -> Unit)? = null,
    placeholder: String,
    placeholderTextField: String,
    shapes: RoundedCornerShape,
    itemBgColor: Color,
    trailing: (@Composable (() -> Unit))? = null,
    placeholderAsValue: Boolean = false,
    overline: @Composable (() -> Unit)? = null
) {
    var showDialog by remember { mutableStateOf(false) }
    var textFieldValue by remember(initialText) { mutableStateOf(initialText) }

    val description: @Composable () -> Unit = when {
        description != null -> {
            {
                Text(
                    description,
                    color = if (placeholderAsValue) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        Color.Unspecified
                    }
                )
            }
        }

        textFieldValue.isNotBlank() -> {
            {
                Text(
                    textFieldValue,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }

        else -> {
            {
                Text(
                    placeholder,
                    color = if (placeholderAsValue) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shapes,
    ) {
        ListItem(
            modifier = Modifier.clickable { showDialog = true },
            colors = ListItemDefaults.colors(
                containerColor = itemBgColor
            ),
            leadingContent = leading,
            content = { Text(headline) },
            supportingContent = description,
            trailingContent = trailing,
            overlineContent = overline
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(headline) },
            text = {
                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    placeholder = { Text(placeholderTextField) },
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onTextSubmitted(textFieldValue)
                        showDialog = false
                    },
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text("Save", style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false },
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text("Cancel", style = MaterialTheme.typography.labelLarge)
                }
            }
        )
    }
}
