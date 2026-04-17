package com.pranshulgg.weathermaster.core.ui.components.tiles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    itemBgColor: Color
) {
    var showDialog by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf(initialText) }

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
            headlineContent = { Text(headline) },
            supportingContent = {
                if (description != null) Text(description)
                else if (textFieldValue.isNotEmpty()) Text(
                    textFieldValue,
                    color = MaterialTheme.colorScheme.tertiary
                ) else {
                    Text(
                        placeholder,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
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
